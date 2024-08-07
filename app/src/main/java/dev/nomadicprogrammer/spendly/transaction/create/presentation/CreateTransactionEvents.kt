package dev.nomadicprogrammer.spendly.transaction.create.presentation

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

sealed class CreateTransactionEvents {
    data class OnTransactionPageLoad(val transactionType: TransactionType) : CreateTransactionEvents()
    data class OnCreateTransactionClicked(val transactionalSms: TransactionalSms) : CreateTransactionEvents()
    data object ClearToastMessage : CreateTransactionEvents()
    data class OnTransactionDateSelected(val date : String) : CreateTransactionEvents()
    data class OnTransactionAmountChanged(val amount : String) : CreateTransactionEvents()
    data class OnTransactionCategorySelected(val category: String) : CreateTransactionEvents()
    data class OnTransactionMediumSelected(val transactionMedium: String) : CreateTransactionEvents()
    data class OnSecondPartyChanged(val secondParty: String) : CreateTransactionEvents()
}