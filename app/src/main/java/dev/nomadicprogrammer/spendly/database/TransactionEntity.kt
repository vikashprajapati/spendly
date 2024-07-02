package dev.nomadicprogrammer.spendly.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.TransactionCategory
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
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
    val category: TransactionCategory? = null,
    val secondParty : String? = null
){
    companion object{
        fun toEntity(transactionalSms: TransactionalSms): TransactionEntity {
            return TransactionEntity(
                type = transactionalSms.type.name,
                transactionDate = transactionalSms.transactionDate,
                bankName = transactionalSms.bankName,
                currency = transactionalSms.currencyAmount.currency,
                amount = transactionalSms.currencyAmount.amount.toFloat(),
                originalSmsId = transactionalSms.smsId?.toLong(),
                category = transactionalSms.category
            )
        }
    }

    fun toModel(smsId : String?= null): TransactionalSms {
        return TransactionalSms.create(
            id = transactionId.toString(),
            type = TransactionType.valueOf(type),
            transactionDate = transactionDate,
            bank = bankName,
            currencyAmount = CurrencyAmount(currency, amount.toDouble()),
            smsId = smsId,
            category = category ?: TransactionCategory.Other,
            transferredTo = if (type == TransactionType.CREDIT.name) secondParty else null,
            receivedFrom = if(type == TransactionType.DEBIT.name) secondParty else null
        )
    }
}
