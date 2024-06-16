package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transactionalSms: Transaction) : Long

    suspend fun saveTransactions(transactionalSms: List<Transaction>)

    suspend fun getAllTransactions(): Flow<List<Transaction>?>

    suspend fun updateTransaction(transaction: Transaction) : Int
}