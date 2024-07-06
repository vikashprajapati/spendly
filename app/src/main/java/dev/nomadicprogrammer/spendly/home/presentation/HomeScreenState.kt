package dev.nomadicprogrammer.spendly.home.presentation

import dev.nomadicprogrammer.spendly.base.TransactionStateHolder

data class HomeScreenState(
    val allTransactionalSms : List<TransactionStateHolder> = mutableListOf(),
    val recentTransactionalSms : List<TransactionStateHolder> = mutableListOf(),
    val todayTransactionalSms: List<TransactionStateHolder> = mutableListOf(),
    val thisWeekTransactionalSms: List<TransactionStateHolder> = mutableListOf(),
    val thisMonthTransactionalSms: List<TransactionStateHolder> = mutableListOf(),
    val thisQuarterTransactionalSms: List<TransactionStateHolder> = mutableListOf(),
    val thisHalfYearTransactionalSms: List<TransactionStateHolder> = mutableListOf(),
    val thisYearTransactionalSms: List<TransactionStateHolder> = mutableListOf(),
    val currentViewBy : ViewBy = ViewBy.Today,
    val currentViewTransactionalSms : List<TransactionStateHolder> = mutableListOf(),
    val selectedTabIndex : Int = 0,
    val selectedTransactionalSms : TransactionStateHolder? = null,
    val progress: Float = 0f
){
}
