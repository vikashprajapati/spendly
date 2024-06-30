package dev.nomadicprogrammer.spendly.home.presentation

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction

sealed class HomeEvent{
    data object PageLoad : HomeEvent()
    data class ViewBySelected(val viewBy: ViewBy, val index : Int) : HomeEvent()

    data class TransactionSelected(val transactionalSms: Transaction) : HomeEvent()

    data object TransactionDialogDismissed : HomeEvent()

    data class TransactionUpdate(val transaction : Transaction) : HomeEvent()

    data object TransactionDetailsDialogLoaded : HomeEvent()

    data object ReadSmsPermissionGranted : HomeEvent()
}