package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model

import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import java.io.Serializable

const val DEFAULT_CURRENCY = "₹"


const val DEBIT = "debit"
const val CREDIT = "credit"

sealed class TransactionalSms(
    open val transactionDate: String?,
    open val bankName: String? = null,
    open val currencyAmount: CurrencyAmount,
    open val originalSms: Sms
) : Serializable{
    data class Debit(
        override val transactionDate: String?,
        val transferredTo: String,
        override val bankName: String? = null,
        override val currencyAmount: CurrencyAmount,
        override val originalSms: Sms
    ) : TransactionalSms(transactionDate, bankName, currencyAmount, originalSms)

    data class Credit(
        override val transactionDate: String?,
        val receivedFrom: String,
        override val bankName: String? = null,
        override val currencyAmount: CurrencyAmount,
        override val originalSms: Sms
    ) : TransactionalSms(transactionDate, bankName, currencyAmount, originalSms)

    companion object {
        fun create(
            type: String,
            sms: Sms,
            currencyAmount: CurrencyAmount,
            bank: String?,
            transactionDate: String?
        ): TransactionalSms? {
            return when(type){
                DEBIT -> Debit(
                    transactionDate = transactionDate, transferredTo = "", bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms,
                )
                CREDIT -> Credit(
                    transactionDate = transactionDate, receivedFrom = "", bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms
                )
                else -> null
            }
        }
    }
}