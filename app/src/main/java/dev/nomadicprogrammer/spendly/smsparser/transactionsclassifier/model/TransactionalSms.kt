package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model

import dev.nomadicprogrammer.spendly.home.data.Transaction
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
    ) : TransactionalSms(DEBIT, transactionDate, bankName, currencyAmount, originalSms, category)

    data class Credit(
        override val transactionDate: String?,
        val receivedFrom: String?,
        override val bankName: String? = null,
        override val currencyAmount: CurrencyAmount,
        override val originalSms: Sms,
        override val category: String? = null
    ) : TransactionalSms(CREDIT, transactionDate, bankName, currencyAmount, originalSms, category)

    companion object {
        fun create(
            type: String,
            sms: Sms,
            currencyAmount: CurrencyAmount,
            bank: String?,
            transactionDate: String?,
            transferredTo: String? = null,
            receivedFrom: String? = null,
            category: String? = null
        ): TransactionalSms? {
            return when(type){
                DEBIT -> Debit(
                    transactionDate = transactionDate, transferredTo = transferredTo, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms, category = category
                )
                CREDIT -> Credit(
                    transactionDate = transactionDate, receivedFrom = receivedFrom, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms, category = category
                )
                else -> null
            }
        }
    }

    fun mapToTransaction(): Transaction? {
        if (this is Debit){
            return Transaction(
                type = type,
                originalSms = originalSms,
                currencyAmount = currencyAmount,
                bankName = bankName,
                transactionDate = transactionDate,
                transferredTo = transferredTo,
                category = category
            )
        }else if(this is Credit){
            Transaction(
                type = type,
                originalSms = originalSms,
                currencyAmount = currencyAmount,
                bankName = bankName,
                transactionDate = transactionDate,
                receivedFrom = receivedFrom,
                category = category
            )
        }

        return null
    }
}