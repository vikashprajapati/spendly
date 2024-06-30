package dev.nomadicprogrammer.spendly.home.presentation

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction

data class HomeScreenState(
    val allTransactions : List<Transaction> = mutableListOf(),
    val recentTransactions : List<Transaction> = mutableListOf(),
    val todayTransaction: List<Transaction> = mutableListOf(),
    val thisWeekTransactions: List<Transaction> = mutableListOf(),
    val thisMonthTransactions: List<Transaction> = mutableListOf(),
    val thisQuarterTransactions: List<Transaction> = mutableListOf(),
    val thisHalfYearTransaction: List<Transaction> = mutableListOf(),
    val thisYearTransactions: List<Transaction> = mutableListOf(),
    val currentViewBy : ViewBy = ViewBy.Today,
    val currentViewTransactions : List<Transaction> = mutableListOf(),
    val selectedTabIndex : Int = 0,
    val dialogTransactionSms : Transaction? = null,
    val progress: Float = 0f
){
}
