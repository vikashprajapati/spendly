package dev.nomadicprogrammer.spendly.transaction.create

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction

sealed class CreateTransactionEvents {
    data class OnCreateTransactionClicked(val transaction: Transaction) : CreateTransactionEvents()
    data object ClearToastMessage : CreateTransactionEvents()
}