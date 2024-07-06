package dev.nomadicprogrammer.spendly.transaction.presentation.view

import dev.nomadicprogrammer.spendly.base.TransactionStateHolder

sealed class TransactionDetailsEvent

data class TransactionDetailsPageLoad(val transactionStateHolder: TransactionStateHolder) : TransactionDetailsEvent()