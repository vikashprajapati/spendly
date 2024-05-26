package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

data class TransactionSmsUiModel(
    val type: TransactionType,
    val transactionDate: String?,
    val bankName: String? = null,
    val currencyAmount: CurrencyAmount,
    val originalSms: Sms,
    val category : String? = null,
    val transferredTo : String? = null,
    val receivedFrom : String? = null
){
    fun mapToTransactionalSms() : TransactionalSms {
        return TransactionalSms.create(
            type = type.value,
            sms = originalSms,
            currencyAmount = currencyAmount,
            bank = bankName,
            transactionDate = transactionDate,
            transferredTo = transferredTo,
            receivedFrom = receivedFrom,
            category = category
        )!!
    }
}

enum class TransactionType(val value: String) {
    DEBIT("Debit"),
    CREDIT("Credit"),
    TRANSFER("Transfer");

    companion object {
        fun fromString(value: String) = values().first { it.value == value }
    }
}