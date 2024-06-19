package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model

import androidx.room.util.copy
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import java.io.Serializable

const val DEFAULT_CURRENCY = "₹"

enum class TransactionType{
    DEBIT,
    CREDIT
}

sealed class Transaction(
    open val id : String?= null,
    val type: TransactionType,
    open val transactionDate: String?,
    open val bankName: String? = null,
    open val currencyAmount: CurrencyAmount,
    open val originalSms: Sms? = null,
    open val category : String? = null
) : Serializable{
    companion object {
        fun create(
            id : String?,
            type: TransactionType,
            sms: Sms? = null,
            currencyAmount: CurrencyAmount,
            bank: String?,
            transactionDate: String?,
            transferredTo: String? = null,
            receivedFrom: String? = null,
            category: String? = null
        ): Transaction {
            return when(type){
                TransactionType.DEBIT -> Debit(
                    id= id,
                    transactionDate = transactionDate, transferredTo = transferredTo, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms, category = category
                )
                TransactionType.CREDIT -> Credit(
                    id = id,
                    transactionDate = transactionDate, receivedFrom = receivedFrom, bankName = bank,
                    currencyAmount = currencyAmount, originalSms = sms, category = category
                )
            }
        }
    }
}

data class Debit(
    override val id: String?,
    override val transactionDate: String?,
    val transferredTo: String?,
    override val bankName: String? = null,
    override val currencyAmount: CurrencyAmount,
    override val originalSms: Sms? = null,
    override val category: String? = null
) : Transaction(id, TransactionType.DEBIT, transactionDate, bankName, currencyAmount, originalSms, category)

data class Credit(
    override val id: String?,
    override val transactionDate: String?,
    val receivedFrom: String?,
    override val bankName: String? = null,
    override val currencyAmount: CurrencyAmount,
    override val originalSms: Sms ?= null,
    override val category: String? = null
) : Transaction(id, TransactionType.CREDIT, transactionDate, bankName, currencyAmount, originalSms, category)