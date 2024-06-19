package dev.nomadicprogrammer.spendly.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    val type: String,
    val transactionDate: String?,
    val bankName: String? = null,
    val amount: Float,
    val currency: String,
    val originalSmsId: Long? = null, // Reference to the original SMS
    val category: String? = null,
    val secondParty : String? = null
){
    companion object{
        fun toEntity(transaction: Transaction): TransactionEntity {
            return TransactionEntity(
                type = transaction.type.name,
                transactionDate = transaction.transactionDate,
                bankName = transaction.bankName,
                currency = transaction.currencyAmount.currency,
                amount = transaction.currencyAmount.amount.toFloat(),
                originalSmsId = transaction.smsId?.toLong(),
                category = transaction.category
            )
        }
    }

    fun toModel(smsId : String?= null): Transaction {
        return Transaction.create(
            id = transactionId.toString(),
            type = TransactionType.valueOf(type),
            transactionDate = transactionDate,
            bank = bankName,
            currencyAmount = CurrencyAmount(currency, amount.toDouble()),
            smsId = smsId,
            category = category,
            transferredTo = if (type == TransactionType.CREDIT.name) secondParty else null,
            receivedFrom = if(type == TransactionType.DEBIT.name) secondParty else null
        )
    }
}
