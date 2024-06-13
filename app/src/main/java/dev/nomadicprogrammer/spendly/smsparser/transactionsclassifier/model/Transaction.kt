package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model

import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import java.io.Serializable

const val DEFAULT_CURRENCY = "₹"

enum class TransactionType{
    DEBIT,
    CREDIT
}

sealed class Transaction(
    val type: TransactionType,
    open val transactionDate: String?,
    open val bankName: String? = null,
    open val currencyAmount: CurrencyAmount,
    open val originalSms: Sms,
    open val category : String? = null
) : Serializable{
    data class Debit(
        override val transactionDate: String?,
        val transferredTo: String?,
        override val bankName: String? = null,
        override val currencyAmount: CurrencyAmount,
        override val originalSms: Sms,
        override val category: String? = null
    ) : Transaction(TransactionType.DEBIT, transactionDate, bankName, currencyAmount, originalSms, category)

    data class Credit(
        override val transactionDate: String?,
        val receivedFrom: String?,
        override val bankName: String? = null,
        override val currencyAmount: CurrencyAmount,
        override val originalSms: Sms,
        override val category: String? = null
    ) : Transaction(TransactionType.CREDIT, transactionDate, bankName, currencyAmount, originalSms, category)

    companion object {
        fun create(
            type: TransactionType,
            sms: Sms,
            currencyAmount: CurrencyAmount,
            bank: String?,
            transactionDate: String?,
            transferredTo: String? = null,
            receivedFrom: String? = null,
            category: String? = null
        ): Transaction {
            return when(type){
                TransactionType.DEBIT -> Debit(
                    transactionDate = transactionDate, transferredTo = transferredTo, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms, category = category
                )
                TransactionType.CREDIT -> Credit(
                    transactionDate = transactionDate, receivedFrom = receivedFrom, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms, category = category
                )
            }
        }
    }
}