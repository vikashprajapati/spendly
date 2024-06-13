package dev.nomadicprogrammer.spendly.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.home.data.GetAllTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spendAnalyserUseCase: SpendAnalyserUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : ViewModel() {
    private val TAG = HomeViewModel::class.java.simpleName

    private val _state = MutableStateFlow(HomeScreenState())
    val state : StateFlow<HomeScreenState> = _state

    private var transactionClassifierJob : Job?= null
    private var getAllTransactionJob : Job?= null

    init {
        launchClassifier()
    }

    private fun launchClassifier(){
        transactionClassifierJob?.cancel()
        transactionClassifierJob = viewModelScope.launch { spendAnalyserUseCase() }
    }

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.PageLoad -> {
                Log.d(TAG, "PageLoad")
                getAllTransactionJob?.cancel()
                getAllTransactionJob = viewModelScope.launch {
                    val allTransactions = getAllTransactionsUseCase()
                    Log.d(TAG, "All transactions: $allTransactions")
                    _state.value = _state.value.copy(allTransactions = allTransactions ?: emptyList())
//                            val takeFrom = DateUtils.Local.getPreviousDate(_state.value.currentViewBy.days)
//                            transactionsViewBy = _state.value.allTransactions.filter {
//                                // Todo: remove transaction date is null check
//                                filterTransactionsByDate(it, takeFrom)
//                            }
                }
            }

            is HomeEvent.ViewBySelected -> {
                viewModelScope.launch {
//                    selectedViewBy = event.viewBy
//                    selectedTabIndex = event.index
//                    if (transactionViewByMap.containsKey(selectedViewBy)) {
//                        transactionsViewBy = transactionViewByMap[selectedViewBy]!!
//                    } else {
//                        val takeFrom = DateUtils.Local.getPreviousDate(selectedViewBy.days)
//                        transactionsViewBy = allTransactions.filter {
//                            filterTransactionsByDate(it, takeFrom)
//                        }
//                        transactionViewByMap[selectedViewBy] = transactionsViewBy
//                    }
                }
            }

            is HomeEvent.TransactionSelected -> {
//                dialogTransactionSms.value = event.transactionalSms
            }

            is HomeEvent.TransactionDialogDismissed -> {
//                dialogTransactionSms.value = null
            }
        }
    }

    private fun filterTransactionsByDate(
        it: Transaction,
        takeFrom: LocalDate?
    ) = it.transactionDate?.let { date ->
        val transactionDate = DateUtils.Local.getLocalDate(date)
        transactionDate.isAfter(takeFrom) || transactionDate.isEqual(takeFrom)
    } ?: false
}

sealed class HomeEvent{
    data object PageLoad : HomeEvent()
    data class ViewBySelected(val viewBy: ViewBy,val index : Int) : HomeEvent()

    data class TransactionSelected(val transactionalSms: Transaction) : HomeEvent()

    data object TransactionDialogDismissed : HomeEvent()
}