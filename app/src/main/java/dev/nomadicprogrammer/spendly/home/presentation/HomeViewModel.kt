package dev.nomadicprogrammer.spendly.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val spendAnalyserController: SpendAnalyserController
) : ViewModel() {
    var transactions by mutableStateOf(emptyList<TransactionalSms>())
        private set

    var recentTransactions by mutableStateOf(emptyList<TransactionalSms>())
        private set

    var selectedViewBy by mutableStateOf(ViewBy.DAILY)
        private set

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.PageLoad -> {
                viewModelScope.launch {
                    spendAnalyserController.launchTransactionalSmsClassifier()
                    transactions = spendAnalyserController.generateReport()
                    val takeFrom = DateUtils.Local.getPreviousDate(ViewBy.DAILY.days)
                    recentTransactions = transactions.filter {
                        // Todo: remove transaction date is null check
                        filterTransactionsByDate(it, takeFrom)
                    }
                }
            }

            is HomeEvent.ViewBySelected -> {
                viewModelScope.launch {
                    selectedViewBy = event.viewBy
                    val takeFrom = DateUtils.Local.getPreviousDate(selectedViewBy.days)
                    recentTransactions = transactions.filter {
                        filterTransactionsByDate(it, takeFrom)
                    }
                }
            }
        }
    }

    private fun filterTransactionsByDate(
        it: TransactionalSms,
        takeFrom: LocalDate?
    ) = it.transactionDate?.let { date ->
        val transactionDate = DateUtils.Local.getLocalDate(date)
        transactionDate.isAfter(takeFrom) || transactionDate.isEqual(takeFrom)
    } ?: false
}

sealed class HomeEvent{
    data object PageLoad : HomeEvent()
    data class ViewBySelected(val viewBy: ViewBy) : HomeEvent()
}