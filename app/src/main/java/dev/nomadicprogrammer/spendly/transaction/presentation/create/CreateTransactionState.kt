package dev.nomadicprogrammer.spendly.transaction.presentation.create

import dev.nomadicprogrammer.spendly.base.TransactionCategoryResource
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType

data class CreateTransactionState(
    val transactionDate: String = "",
    val transactionAmount: String = "",
    val transactionCategory: String = "",
    val transactionMedium: String = "",
    val transactionType: TransactionType = TransactionType.DEBIT,
    val secondParty: String = "",
    val enableCreateTransactionButton: Boolean = false,
    val error : Pair<String?, String?>? = null,
    val categories : List<TransactionCategoryResource> = emptyList()
)