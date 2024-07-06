package dev.nomadicprogrammer.spendly.home.presentation

import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

sealed class HomeEvent{
    data object PageLoad : HomeEvent()
    data class ViewBySelected(val viewBy: ViewBy, val index : Int) : HomeEvent()

    data class TransactionSelected(val transactionStateHolder: TransactionStateHolder) : HomeEvent()

    data object TransactionDialogDismissed : HomeEvent()

    data class TransactionUpdate(val transactionStateHolder : TransactionStateHolder) : HomeEvent()

    data class TransactionDetailsPageLoaded(val transactionStateHolder: TransactionStateHolder) : HomeEvent()

    data object ReadSmsPermissionGranted : HomeEvent()

    data object ClearToastMessage : HomeEvent()
}