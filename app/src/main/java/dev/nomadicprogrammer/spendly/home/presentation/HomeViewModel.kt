package dev.nomadicprogrammer.spendly.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.home.data.GetAllTransactionsUseCase
import dev.nomadicprogrammer.spendly.home.data.OriginalSmsFetchUseCase
import dev.nomadicprogrammer.spendly.home.data.UpdateTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Credit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Debit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spendAnalyserUseCase: SpendAnalyserUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val updateTransactionsUseCase: UpdateTransactionsUseCase,
    private val originalSmsFetchUseCase: OriginalSmsFetchUseCase
) : ViewModel() {
    private val TAG = HomeViewModel::class.java.simpleName

    private val _state = MutableStateFlow(HomeScreenState())
    val state : StateFlow<HomeScreenState> = _state

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
                            updateState(it?: emptyList())
                        }
                }
            }

            is HomeEvent.ViewBySelected -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(currentViewBy = event.viewBy, selectedTabIndex = event.index, currentViewTransactions = currentViewTransactions(event.viewBy))
                }
            }

            is HomeEvent.TransactionSelected -> { _state.value = _state.value.copy(dialogTransactionSms = event.transactionalSms) }

            is HomeEvent.TransactionDialogDismissed -> { _state.value = _state.value.copy(dialogTransactionSms = null) }

            is HomeEvent.TransactionUpdate -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val rowsUpdated = updateTransactionsUseCase(event.transaction)
                    Log.d(TAG, "Rows updated: $rowsUpdated")
                }
            }

            is HomeEvent.TransactionDetailsDialogLoaded -> {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d(TAG, "reading original sms for smsId: ${_state.value.dialogTransactionSms?.smsId}")
                    val originalSms = _state.value.dialogTransactionSms?.smsId?.let {
                        originalSmsFetchUseCase.invoke(it)
                    }
                    Log.d(TAG, "Original sms: $originalSms")
                    val transactionalSms = when(_state.value.dialogTransactionSms!!){
                        is Debit -> (_state.value.dialogTransactionSms as Debit).copy(originalSms = originalSms)
                        is Credit -> (_state.value.dialogTransactionSms as Credit).copy(originalSms = originalSms)
                    }
                    _state.value = _state.value.copy(dialogTransactionSms = transactionalSms)
                }
            }

            is HomeEvent.ReadSmsPermissionGranted -> {
                launchClassifier()
            }
        }
    }

    private fun updateState(allTransactions: List<Transaction>) {
        val sortedTransactions = allTransactions.reversed()
        _state.value = _state.value.copy(
            allTransactions = sortedTransactions,
            recentTransactions = sortedTransactions.take(5),
            todayTransaction = sortedTransactions.filter { filterTransactionsByDate(it, DateUtils.Local.getPreviousDate(1)) },
            thisWeekTransactions = sortedTransactions.filter { filterTransactionsByDate(it, DateUtils.Local.getPreviousDate(7)) },
            thisMonthTransactions = sortedTransactions.filter { filterTransactionsByDate(it, DateUtils.Local.getPreviousDate(30)) }, // TODO: consider 30 or 31 or leap year,
            thisQuarterTransactions = sortedTransactions.filter { filterTransactionsByDate(it, DateUtils.Local.getPreviousDate(90)) },
            thisHalfYearTransaction = sortedTransactions.filter { filterTransactionsByDate(it, DateUtils.Local.getPreviousDate(180)) },
            thisYearTransactions = sortedTransactions.filter { filterTransactionsByDate(it, DateUtils.Local.getPreviousDate(365)) },
            progress = 100f
        )

        _state.value = _state.value.copy(currentViewTransactions = currentViewTransactions(_state.value.currentViewBy))
    }

    private fun currentViewTransactions(currentViewBy : ViewBy) = when(currentViewBy){
        ViewBy.Today -> _state.value.todayTransaction
        ViewBy.WEEKLY -> _state.value.thisWeekTransactions
        ViewBy.MONTHLY -> _state.value.thisMonthTransactions
        ViewBy.Quarter -> _state.value.thisQuarterTransactions
        ViewBy.MidYear -> _state.value.thisHalfYearTransaction
        ViewBy.Yearly -> _state.value.thisYearTransactions
    }

    private fun filterTransactionsByDate(
        it: Transaction,
        takeFrom: LocalDate?
    ) = it.transactionDate?.let { date ->
        val transactionDate = DateUtils.Local.getLocalDate(date)
        transactionDate.isAfter(takeFrom) || transactionDate.isEqual(takeFrom)
    } ?: false
}
