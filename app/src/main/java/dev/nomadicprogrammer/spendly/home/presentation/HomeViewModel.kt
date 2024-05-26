package dev.nomadicprogrammer.spendly.home.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.home.data.TransactionSmsUiModel
import dev.nomadicprogrammer.spendly.home.data.TransactionUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.ui.components.Account
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val spendAnalyserController: SpendAnalyserController,
    private val transactionUseCase: TransactionUseCase
) : ViewModel() {
    private val TAG = HomeViewModel::class.java.simpleName

    private var allTransactions by mutableStateOf(emptyList<TransactionSmsUiModel>())

    var recentTransactions = mutableStateOf(emptyList<TransactionSmsUiModel>())
        get() = mutableStateOf(transactionsViewBy.take(5))
        private set

    var transactionsViewBy by mutableStateOf(emptyList<TransactionSmsUiModel>())
        private set

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    private var selectedViewBy = ViewBy.DAILY

    var income = mutableStateOf(Account.Income(0f))
        get() {
            viewModelScope.launch {
                field.value = Account.Income(transactionsViewBy
                    .filterIsInstance<TransactionalSms.Credit>()
                    .sumOf { it.currencyAmount.amount }
                    .toFloat()
                )
            }
            return field
        }
        private set

    var expense = mutableStateOf(Account.Expense(0f))
        get() {
            viewModelScope.launch {
                field.value = Account.Expense(transactionsViewBy
                    .filterIsInstance<TransactionalSms.Debit>()
                    .sumOf { it.currencyAmount.amount }
                    .toFloat()
                )
            }
            return field
        }
        private set

    private val transactionViewByMap = mutableMapOf<ViewBy, List<TransactionSmsUiModel>>()

    val dialogTransactionSms = mutableStateOf<TransactionSmsUiModel?>(null)

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.PageLoad -> {
                Log.d(TAG, "PageLoad")
                viewModelScope.launch {
                    spendAnalyserController.launchTransactionalSmsClassifier()

                    transactionUseCase
                        .getTransactions()
                        .collect{
                            allTransactions = it ?: emptyList()
                            val takeFrom = DateUtils.Local.getPreviousDate(selectedViewBy.days)
                            transactionsViewBy = allTransactions.filter {
                                // Todo: remove transaction date is null check
                                filterTransactionsByDate(it, takeFrom)
                            }
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

            is HomeEvent.TransactionSelected -> {
                dialogTransactionSms.value = event.transactionalSms
            }

            is HomeEvent.TransactionDialogDismissed -> {
                dialogTransactionSms.value = null
            }
        }
    }

    private fun filterTransactionsByDate(
        it: TransactionSmsUiModel,
        takeFrom: LocalDate?
    ) = it.transactionDate?.let { date ->
        val transactionDate = DateUtils.Local.getLocalDate(date)
        transactionDate.isAfter(takeFrom) || transactionDate.isEqual(takeFrom)
    } ?: false
}

sealed class HomeEvent{
    data object PageLoad : HomeEvent()
    data class ViewBySelected(val viewBy: ViewBy,val index : Int) : HomeEvent()

    data class TransactionSelected(val transactionalSms: TransactionSmsUiModel) : HomeEvent()

    data object TransactionDialogDismissed : HomeEvent()
}