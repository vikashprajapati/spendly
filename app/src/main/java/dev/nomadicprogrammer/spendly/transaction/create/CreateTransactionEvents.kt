package dev.nomadicprogrammer.spendly.transaction.create

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction

sealed class CreateTransactionEvents {
    data class OnCreateTransactionClicked(val transaction: Transaction) : CreateTransactionEvents()
    data object ClearToastMessage : CreateTransactionEvents()
    data class OnTransactionDateSelected(val date : String) : CreateTransactionEvents()
    data class OnTransactionAmountChanged(val amount : String) : CreateTransactionEvents()
    data class OnTransactionCategorySelected(val category: String) : CreateTransactionEvents()
    data class OnTransactionMediumSelected(val transactionMedium: String) : CreateTransactionEvents()
    data class OnSecondPartyChanged(val secondParty: String) : CreateTransactionEvents()
}