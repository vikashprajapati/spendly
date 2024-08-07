package dev.nomadicprogrammer.spendly.transaction.view.presentation

import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms

data class TransactionDetailsState(
    val originalSms: Sms? = null,
    val transactionDeleted: Boolean = false
)