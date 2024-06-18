package dev.nomadicprogrammer.spendly.transaction.create

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction

sealed class CreateTransactionEvents {
    data class onCreateTransactionClicked(val transaction: Transaction) : CreateTransactionEvents()
    data object clearToastMessage : CreateTransactionEvents()
}