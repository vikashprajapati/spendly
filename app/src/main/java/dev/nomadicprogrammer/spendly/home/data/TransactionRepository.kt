package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transactionalSms: TransactionalSms) : Long

    suspend fun saveTransactions(transactionalSms: List<TransactionalSms>)

    suspend fun getAllTransactions(): Flow<List<TransactionalSms>?>

    suspend fun updateTransaction(transactionalSms: TransactionalSms) : Int

    suspend fun deleteTransaction(transactionalSms: TransactionalSms) : Int
}