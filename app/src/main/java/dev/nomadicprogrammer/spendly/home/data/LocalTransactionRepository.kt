package dev.nomadicprogrammer.spendly.home.data

import android.util.Log
import dev.nomadicprogrammer.spendly.database.TransactionDao
import dev.nomadicprogrammer.spendly.home.data.mappers.TransactionMapper
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class LocalTransactionRepository(
    private val transactionDao: TransactionDao,
    private val transactionMapper: TransactionMapper,
    private val smsInbox: SmsDataSource
) : TransactionRepository {
    private val TAG = LocalTransactionRepository::class.java.simpleName

    override suspend fun saveTransaction(transactionalSms: TransactionSmsUiModel) {
        val transactionEntity = transactionMapper.toTransactionEntity(transactionalSms)
        transactionDao.insertTransaction(transactionEntity)
    }

    override suspend fun saveTransactions(transactionalSms: List<TransactionSmsUiModel>) {
        val transactionEntities = transactionalSms.map { transactionMapper.toTransactionEntity(it) }
        transactionDao.insertTransactions(transactionEntities)
    }

    override suspend fun getTransactions(): Flow<List<TransactionSmsUiModel>?> {
        val transactionEntities = transactionDao.getAllTransactions().first()
        val smsIds = transactionEntities.map { it.originalSmsId.toInt() }
        val originalSmsList = smsInbox.getSmsByIds(smsIds)
        Log.d(TAG, "getTransactions: originalSmsList: $originalSmsList")
        val transactionModel : Flow<List<TransactionSmsUiModel>?> =  flow { emit(originalSmsList?.let { transactionMapper.toTransactions(transactionEntities, it) }) }
        Log.d(TAG, "getTransactions: transactionModel: $transactionModel")
        return transactionModel
    }
}