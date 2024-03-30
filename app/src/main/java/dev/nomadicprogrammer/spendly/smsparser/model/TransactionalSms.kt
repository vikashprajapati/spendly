package dev.nomadicprogrammer.spendly.smsparser.model

import dev.nomadicprogrammer.spendly.smsparser.usecases.TransactionalSmsClassifier

sealed class TransactionalSms(
    open val transactionDate: String?,
    open val bankName: String? = null,
    open val currencyAmount: TransactionalSmsClassifier.CurrencyAmount,
    open val originalSms: Sms
){
    data class Debit(
        override val transactionDate: String?,
        val transferredTo: String,
        override val bankName: String? = null,
        override val currencyAmount: TransactionalSmsClassifier.CurrencyAmount,
        override val originalSms: Sms
    ) : TransactionalSms(transactionDate, bankName, currencyAmount, originalSms)

    data class Credit(
        override val transactionDate: String?,
        val receivedFrom: String,
        override val bankName: String? = null,
        override val currencyAmount: TransactionalSmsClassifier.CurrencyAmount,
        override val originalSms: Sms
    ) : TransactionalSms(transactionDate, bankName, currencyAmount, originalSms)
}

const val DEFAULT_CURRENCY = "INR"