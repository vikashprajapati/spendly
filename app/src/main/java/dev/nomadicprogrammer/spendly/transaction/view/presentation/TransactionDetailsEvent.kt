package dev.nomadicprogrammer.spendly.transaction.view.presentation

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

sealed class TransactionDetailsEvent

data class TransactionDetailsPageLoad(val originalSmsId: String?= null) : TransactionDetailsEvent()

data class OnDeleteClicked(val transactionalSms: TransactionalSms) : TransactionDetailsEvent()

data object ClearToast : TransactionDetailsEvent()