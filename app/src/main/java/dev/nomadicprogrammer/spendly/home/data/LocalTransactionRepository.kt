package dev.nomadicprogrammer.spendly.home.data

import android.util.Log
import dev.nomadicprogrammer.spendly.database.TransactionDao
import dev.nomadicprogrammer.spendly.database.TransactionEntity
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsDataSource
import dev.nomadicprogrammer.spendly.smsparser.common.model.DEFAULT_UNDEFINED_SMS
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val smsInbox: SmsDataSource<Transaction>
) : TransactionRepository {
    private val TAG = LocalTransactionRepository::class.java.simpleName

    override suspend fun saveTransaction(transactionalSms: Transaction) : Long {
        val transactionEntity = TransactionEntity.toEntity(transactionalSms)
        return transactionDao.insertTransaction(transactionEntity)
    }

    override suspend fun saveTransactions(transactionalSms: List<Transaction>) {
        val transactionEntities = transactionalSms.map { TransactionEntity.toEntity(it) }
        transactionDao.insertTransactions(transactionEntities)
    }

    override suspend fun getAllTransactions(): Flow<List<Transaction>> = flow{
        transactionDao.getAllTransactions().collect{ transactionEntities->
            Log.d(TAG, "getAllTransactions: transactionEntities size: ${transactionEntities.size}")
            val smsIds = transactionEntities.map { it.originalSmsId.toInt() }
            val originalSmsList = smsInbox.getSmsByIds(smsIds)?: List(transactionEntities.size){DEFAULT_UNDEFINED_SMS}
            Log.d(TAG, "getAllTransactions: originalSmsList: ${originalSmsList}")
            Log.d(TAG, "getAllTransactions: originalSmsList: ${originalSmsList.size}")
            val transactionModel =  transactionEntities.zip(originalSmsList){transactionEntity, originalSms ->
                transactionEntity.toModel(originalSms?:DEFAULT_UNDEFINED_SMS)
            }
            Log.d(TAG, "getAllTransactions: transactionModel: $transactionModel")
            emit(transactionModel)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Int {
        return transactionDao.updateTransactionCategory(transaction.id!!, transaction.category!!)
    }
}