package dev.nomadicprogrammer.spendly.home.data

import android.util.Log
import dev.nomadicprogrammer.spendly.database.TransactionDao
import dev.nomadicprogrammer.spendly.database.TransactionEntity
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsDataSource
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LocalTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val smsInbox: SmsDataSource<Transaction>
) : TransactionRepository {
    private val TAG = LocalTransactionRepository::class.java.simpleName

    override suspend fun saveTransaction(transactionalSms: Transaction) {
        val transactionEntity = TransactionEntity.toEntity(transactionalSms)
        transactionDao.insertTransaction(transactionEntity)
    }

    override suspend fun saveTransactions(transactionalSms: List<Transaction>) {
        val transactionEntities = transactionalSms.map { TransactionEntity.toEntity(it) }
        transactionDao.insertTransactions(transactionEntities)
    }

    override suspend fun getAllTransactions(): List<Transaction> {
        val transactionEntities = transactionDao.getAllTransactions().first()
        val smsIds = transactionEntities.map { it.originalSmsId.toInt() }
        val originalSmsList = smsInbox.getSmsByIds(smsIds)?: emptyList()
        Log.d(TAG, "getAllTransactions: originalSmsList: $originalSmsList")
        val transactionModel =  transactionEntities.zip(originalSmsList){transactionEntity, originalSms -> transactionEntity.toModel(originalSms) }
        Log.d(TAG, "getAllTransactions: transactionModel: $transactionModel")
        return transactionModel
    }
}