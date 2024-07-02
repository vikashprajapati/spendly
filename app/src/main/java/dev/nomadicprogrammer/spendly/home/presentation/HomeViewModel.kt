package dev.nomadicprogrammer.spendly.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.base.TransactionCategoryResource
import dev.nomadicprogrammer.spendly.base.TransactionCategoryResourceProvider
import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import dev.nomadicprogrammer.spendly.home.data.GetAllTransactionsUseCase
import dev.nomadicprogrammer.spendly.home.data.OriginalSmsFetchUseCase
import dev.nomadicprogrammer.spendly.home.data.UpdateTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Credit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Debit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spendAnalyserUseCase: SpendAnalyserUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val updateTransactionsUseCase: UpdateTransactionsUseCase,
    private val originalSmsFetchUseCase: OriginalSmsFetchUseCase,
    val transactionCategoryResource: List<TransactionCategoryResource>,
    private val transactionCategoryResourceProvider: TransactionCategoryResourceProvider
) : ViewModel() {
    private val TAG = HomeViewModel::class.java.simpleName

    private val _state = MutableStateFlow(HomeScreenState())
    val state : StateFlow<HomeScreenState> = _state

    private val _toastMessage = MutableSharedFlow<String?>()
    val toastMessage : SharedFlow<String?> = _toastMessage

    private var transactionClassifierJob : Job?= null
    private var getAllTransactionJob : Job?= null

    init {
        Log.d(TAG, "HomeViewModel created")
        launchClassifier()
    }

    private fun launchClassifier(){
        transactionClassifierJob?.cancel()
        transactionClassifierJob = viewModelScope.launch(Dispatchers.IO) {
            val progressJob = launch {
                spendAnalyserUseCase.progress.collect{
                    _state.value = _state.value.copy(progress = it)
                }
            }
            spendAnalyserUseCase.invoke()
            progressJob.cancel()
        }
    }

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.PageLoad -> {
                Log.d(TAG, "PageLoad")
                getAllTransactionJob?.cancel()
                getAllTransactionJob = viewModelScope.launch(Dispatchers.IO) {
                    getAllTransactionsUseCase()
                        .collect{
                            Log.d(TAG, "All transactions: $it")
                            updateState(it?.reversed()?: emptyList())
                        }
                }
            }

            is HomeEvent.ViewBySelected -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(currentViewBy = event.viewBy, selectedTabIndex = event.index, currentViewTransactionalSms = currentViewTransactions(event.viewBy))
                }
            }

            is HomeEvent.TransactionSelected -> { _state.value = _state.value.copy(dialogTransactionalSmsSms = event.transactionStateHolder) }

            is HomeEvent.TransactionDialogDismissed -> { _state.value = _state.value.copy(dialogTransactionalSmsSms = null) }

            is HomeEvent.TransactionUpdate -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val rowsUpdated = updateTransactionsUseCase(event.transactionStateHolder.transactionalSms)
                    Log.d(TAG, "Rows updated: $rowsUpdated")
                    if(rowsUpdated > 0){
                        val allTransactions = _state.value.allTransactionalSms.toMutableList()
                        val newTransactionStateHolder = TransactionStateHolder(event.transactionStateHolder.transactionalSms, transactionCategoryResourceProvider.getResource(event.transactionStateHolder.transactionalSms.category))
                        allTransactions[allTransactions.indexOfFirst { it.transactionalSms.id == event.transactionStateHolder.transactionalSms.id }] = newTransactionStateHolder
                        _state.value = _state.value.copy(dialogTransactionalSmsSms = null)
                        updateState(allTransactions.toList())
                        _toastMessage.emit("Transaction updated")
                    }
                }
            }

            is HomeEvent.ClearToastMessage -> {
                viewModelScope.launch {
                    _toastMessage.emit(null)
                }
            }

            is HomeEvent.TransactionDetailsDialogLoaded -> {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d(TAG, "reading original sms for smsId: ${_state.value.dialogTransactionalSmsSms?.transactionalSms?.smsId}")
                    val originalSms = _state.value.dialogTransactionalSmsSms?.transactionalSms?.smsId?.let {
                        originalSmsFetchUseCase.invoke(it)
                    }
                    Log.d(TAG, "Original sms: $originalSms")
                    val transactionalSmsNew = when(val transactionalSms = _state.value.dialogTransactionalSmsSms?.transactionalSms!!){
                        is Debit -> transactionalSms.copy(originalSms = originalSms)
                        is Credit -> transactionalSms.copy(originalSms = originalSms)
                    }
                    _state.value = _state.value.copy(dialogTransactionalSmsSms = _state.value.dialogTransactionalSmsSms?.copy(transactionalSms = transactionalSmsNew))
                }
            }

            is HomeEvent.ReadSmsPermissionGranted -> {
                launchClassifier()
            }
        }
    }

    private fun updateState(sortedTransactions: List<TransactionStateHolder>) {
        _state.value = _state.value.copy(
            allTransactionalSms = sortedTransactions,
            recentTransactionalSms = sortedTransactions.take(5),
            todayTransactionalSms = sortedTransactions.filter { filterTransactionsByDate(it.transactionalSms, DateUtils.Local.getPreviousDate(1)) },
            thisWeekTransactionalSms = sortedTransactions.filter { filterTransactionsByDate(it.transactionalSms, DateUtils.Local.getPreviousDate(7)) },
            thisMonthTransactionalSms = sortedTransactions.filter { filterTransactionsByDate(it.transactionalSms, DateUtils.Local.getPreviousDate(30)) }, // TODO: consider 30 or 31 or leap year,
            thisQuarterTransactionalSms = sortedTransactions.filter { filterTransactionsByDate(it.transactionalSms, DateUtils.Local.getPreviousDate(90)) },
            thisHalfYearTransactionalSms = sortedTransactions.filter { filterTransactionsByDate(it.transactionalSms, DateUtils.Local.getPreviousDate(180)) },
            thisYearTransactionalSms = sortedTransactions.filter { filterTransactionsByDate(it.transactionalSms, DateUtils.Local.getPreviousDate(365)) },
            progress = 100f
        )

        _state.value = _state.value.copy(currentViewTransactionalSms = currentViewTransactions(_state.value.currentViewBy))
    }

    private fun currentViewTransactions(currentViewBy : ViewBy) = when(currentViewBy){
        ViewBy.Today -> _state.value.todayTransactionalSms
        ViewBy.WEEKLY -> _state.value.thisWeekTransactionalSms
        ViewBy.MONTHLY -> _state.value.thisMonthTransactionalSms
        ViewBy.Quarter -> _state.value.thisQuarterTransactionalSms
        ViewBy.MidYear -> _state.value.thisHalfYearTransactionalSms
        ViewBy.Yearly -> _state.value.thisYearTransactionalSms
    }

    private fun filterTransactionsByDate(
        it: TransactionalSms,
        takeFrom: LocalDate?
    ) = it.transactionDate?.let { date ->
        val transactionDate = DateUtils.Local.getLocalDate(date)
        transactionDate.isAfter(takeFrom) || transactionDate.isEqual(takeFrom)
    } ?: false
}
