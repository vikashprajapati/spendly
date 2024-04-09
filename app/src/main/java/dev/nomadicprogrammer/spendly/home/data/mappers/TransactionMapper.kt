package dev.nomadicprogrammer.spendly.home.data.mappers

import dev.nomadicprogrammer.spendly.database.TransactionEntity
import dev.nomadicprogrammer.spendly.home.data.Transaction
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms

class TransactionMapper(
    private val smsInbox: SmsInbox
) {
    fun toTransactionEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            type = transaction.type,
            transactionDate = transaction.transactionDate,
            bankName = transaction.bankName,
            currency = transaction.currencyAmount.currency,
            amount = transaction.currencyAmount.amount.toFloat(),
            originalSmsId = transaction.originalSms.id.toLong(),
            category = transaction.category
        )
    }

    fun toTransaction(transactionEntity: TransactionEntity): Transaction {
        val originalSms : Sms = smsInbox.getSmsById(transactionEntity.originalSmsId.toInt())!!
        return Transaction(
            type = transactionEntity.type,
            transactionDate = transactionEntity.transactionDate,
            bankName = transactionEntity.bankName,
            currencyAmount = CurrencyAmount(transactionEntity.currency, transactionEntity.amount.toDouble()),
            originalSms = originalSms,
            category = transactionEntity.category
        )
    }

    fun toTransactions(transactionEntity: List<TransactionEntity>, originalSmsList : List<Sms>): List<Transaction> {
        return transactionEntity.mapIndexed { index, it ->
            Transaction(
                type = it.type,
                transactionDate = it.transactionDate,
                bankName = it.bankName,
                currencyAmount = CurrencyAmount(it.currency, it.amount.toDouble()),
                originalSms = originalSmsList[index],
                category = it.category
            )
        }
    }
}