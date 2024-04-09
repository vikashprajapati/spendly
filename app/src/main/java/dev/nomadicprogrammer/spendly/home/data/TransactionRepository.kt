package dev.nomadicprogrammer.spendly.home.data

interface TransactionRepository {
    suspend fun saveTransaction(transactionalSms: Transaction)

    suspend fun saveTransactions(transactionalSms: List<Transaction>)

    suspend fun getTransactions(): List<Transaction>?
}