package dev.nomadicprogrammer.spendly.home.data.mappers

import android.util.Log
import dev.nomadicprogrammer.spendly.database.TransactionEntity
import dev.nomadicprogrammer.spendly.home.data.TransactionSmsUiModel
import dev.nomadicprogrammer.spendly.home.data.TransactionType
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms

class TransactionMapper(
    private val smsInbox: SmsInbox
) {
    fun toTransactionEntity(transactionSmsUiModel: TransactionSmsUiModel): TransactionEntity {
        return TransactionEntity(
            type = transactionSmsUiModel.type.value,
            transactionDate = transactionSmsUiModel.transactionDate,
            bankName = transactionSmsUiModel.bankName,
            currency = transactionSmsUiModel.currencyAmount.currency,
            amount = transactionSmsUiModel.currencyAmount.amount.toFloat(),
            originalSmsId = transactionSmsUiModel.originalSms.id.toLong(),
            category = transactionSmsUiModel.category
        )
    }

    fun toTransaction(transactionEntity: TransactionEntity): TransactionSmsUiModel {
        val originalSms : Sms = smsInbox.getSmsById(transactionEntity.originalSmsId.toInt())!!
        return TransactionSmsUiModel(
            type = TransactionType.valueOf(transactionEntity.type),
            transactionDate = transactionEntity.transactionDate,
            bankName = transactionEntity.bankName,
            currencyAmount = CurrencyAmount(transactionEntity.currency, transactionEntity.amount.toDouble()),
            originalSms = originalSms,
            category = transactionEntity.category
        )
    }

    fun toTransactions(transactionEntity: List<TransactionEntity>, originalSmsList : List<Sms>): List<TransactionSmsUiModel> {
        return transactionEntity.mapIndexed { index, it ->
            Log.d("TransactionMapper", "toTransactions: }")
            TransactionSmsUiModel(
                type = TransactionType.fromString(it.type),
                transactionDate = it.transactionDate,
                bankName = it.bankName,
                currencyAmount = CurrencyAmount(it.currency, it.amount.toDouble()),
                originalSms = Sms("", "", "", 0),
                category = it.category
            )
        }
    }
}