package dev.nomadicprogrammer.spendly.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.ui.components.Account
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val spendAnalyserController: SpendAnalyserController
) : ViewModel() {
    private var allTransactions by mutableStateOf(emptyList<TransactionalSms>())

    var recentTransactions = mutableStateOf(emptyList<TransactionalSms>())
        get() = mutableStateOf(transactionsViewBy.take(5))
        private set

    var transactionsViewBy by mutableStateOf(emptyList<TransactionalSms>())
        private set

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    var selectedViewBy by mutableStateOf(ViewBy.DAILY)
        private set

    var income = mutableStateOf(Account.Income(0f))
        get() {
            field.value = Account.Income(transactionsViewBy
                .filterIsInstance<TransactionalSms.Credit>()
                .sumOf { it.currencyAmount.amount }
                .toFloat()
            )
            return field
        }
        private set

    var expense = mutableStateOf(Account.Expense(0f))
        get() {
            field.value = Account.Expense(transactionsViewBy
                .filterIsInstance<TransactionalSms.Debit>()
                .sumOf { it.currencyAmount.amount }
                .toFloat()
            )
            return field
        }
        private set

    private val transactionViewByMap = mutableMapOf<ViewBy, List<TransactionalSms>>()

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.PageLoad -> {
                viewModelScope.launch {
                    spendAnalyserController.launchTransactionalSmsClassifier()
                    allTransactions = spendAnalyserController.generateReport()
                    val takeFrom = DateUtils.Local.getPreviousDate(ViewBy.DAILY.days)
                    transactionsViewBy = allTransactions.filter {
                        // Todo: remove transaction date is null check
                        filterTransactionsByDate(it, takeFrom)
                    }
                }
            }

            is HomeEvent.ViewBySelected -> {
                viewModelScope.launch {
                    selectedViewBy = event.viewBy
                    selectedTabIndex = event.index
                    if (transactionViewByMap.containsKey(selectedViewBy)) {
                        transactionsViewBy = transactionViewByMap[selectedViewBy]!!
                    } else {
                        val takeFrom = DateUtils.Local.getPreviousDate(selectedViewBy.days)
                        transactionsViewBy = allTransactions.filter {
                            filterTransactionsByDate(it, takeFrom)
                        }
                        transactionViewByMap[selectedViewBy] = transactionsViewBy
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
    data class ViewBySelected(val viewBy: ViewBy,val index : Int) : HomeEvent()
}