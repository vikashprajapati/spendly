package dev.nomadicprogrammer.spendly.home.data

import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transactionalSms: TransactionSmsUiModel)

    suspend fun saveTransactions(transactionalSms: List<TransactionSmsUiModel>)

    suspend fun getTransactions(): Flow<List<TransactionSmsUiModel>?>
}