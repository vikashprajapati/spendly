package dev.nomadicprogrammer.spendly.home.presentation

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy

data class HomeScreenState(
    val allTransactions : List<Transaction> = mutableListOf(),
    val recentTransactions : List<Transaction> = mutableListOf(),
    val todayTransaction: List<Transaction> = mutableListOf(),
    val thisWeekTransactions: List<Transaction> = mutableListOf(),
    val thisMonthTransactions: List<Transaction> = mutableListOf(),
    val thisQuarterTransactions: List<Transaction> = mutableListOf(),
    val thisHalfYearTransaction: List<Transaction> = mutableListOf(),
    val thisYearTransactions: List<Transaction> = mutableListOf(),
    val currentViewBy : ViewBy = ViewBy.DAILY,
    val currentViewTransactions : List<Transaction> = mutableListOf(),
    val selectedTabIndex : Int = 0,
    val dialogTransactionSms : Transaction? = null
){
}