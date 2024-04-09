package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

class StoreTransactionUseCase(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend fun saveTransaction(transaction : Transaction) {
        transactionEntityRepository.saveTransaction(transaction)
    }

    suspend fun saveTransactions(transactions: List<Transaction>) {
        transactionEntityRepository.saveTransactions(transactions)
    }

    suspend fun getTransactions(): List<Transaction>? {
        return transactionEntityRepository.getTransactions()
    }
}

data class Transaction(
    val type: String,
    val transactionDate: String?,
    val bankName: String? = null,
    val currencyAmount: CurrencyAmount,
    val originalSms: Sms,
    val category : String? = null,
    val transferredTo : String? = null,
    val receivedFrom : String? = null
){
    fun mapToTransactionalSms() : TransactionalSms{
        return TransactionalSms.create(
            type = type,
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