package dev.nomadicprogrammer.spendly.transaction.create

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType

data class CreateTransactionState(
    val transactionDate: String = "",
    val transactionAmount: String = "0",
    val transactionCategory: String = "",
    val transactionType: TransactionType = TransactionType.DEBIT,
    val secondParty: String = ""
)