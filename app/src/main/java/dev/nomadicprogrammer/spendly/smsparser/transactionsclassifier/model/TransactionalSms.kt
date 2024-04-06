package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model

import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import java.io.Serializable

const val DEFAULT_CURRENCY = "₹"


const val DEBIT = "Debit"
const val CREDIT = "Credit"

sealed class TransactionalSms(
    val type: String,
    open val transactionDate: String?,
    open val bankName: String? = null,
    open val currencyAmount: CurrencyAmount,
    open val originalSms: Sms
) : Serializable{
    data class Debit(
        override val transactionDate: String?,
        val transferredTo: String?,
        override val bankName: String? = null,
        override val currencyAmount: CurrencyAmount,
        override val originalSms: Sms
    ) : TransactionalSms(DEBIT, transactionDate, bankName, currencyAmount, originalSms)

    data class Credit(
        override val transactionDate: String?,
        val receivedFrom: String?,
        override val bankName: String? = null,
        override val currencyAmount: CurrencyAmount,
        override val originalSms: Sms
    ) : TransactionalSms(CREDIT, transactionDate, bankName, currencyAmount, originalSms)

    companion object {
        fun create(
            type: String,
            sms: Sms,
            currencyAmount: CurrencyAmount,
            bank: String?,
            transactionDate: String?,
            transferredTo: String? = null,
            receivedFrom: String? = null
        ): TransactionalSms? {
            return when(type){
                DEBIT -> Debit(
                    transactionDate = transactionDate, transferredTo = transferredTo, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms
                )
                CREDIT -> Credit(
                    transactionDate = transactionDate, receivedFrom = receivedFrom, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms
                )
                else -> null
            }
        }
    }
}