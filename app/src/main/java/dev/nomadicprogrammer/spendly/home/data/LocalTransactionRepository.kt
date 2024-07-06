package dev.nomadicprogrammer.spendly.home.data

import android.util.Log
import dev.nomadicprogrammer.spendly.database.TransactionDao
import dev.nomadicprogrammer.spendly.database.TransactionEntity
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    private val TAG = LocalTransactionRepository::class.java.simpleName

    override suspend fun saveTransaction(transactionalSms: TransactionalSms) : Long {
        val transactionEntity = TransactionEntity.toEntity(transactionalSms)
        return transactionDao.insertTransaction(transactionEntity)
    }

    override suspend fun saveTransactions(transactionalSms: List<TransactionalSms>) {
        val transactionEntities = transactionalSms.map { TransactionEntity.toEntity(it) }
        transactionDao.insertTransactions(transactionEntities)
    }

    override suspend fun getAllTransactions(): Flow<List<TransactionalSms>> = flow{
        transactionDao.getAllTransactions().collect{ transactionEntities->
            Log.d(TAG, "getAllTransactions: transactionEntities size: ${transactionEntities.size}")
            Log.d(TAG, "getAllTransactions: transactionEntities: $transactionEntities")
            val transactionModels = transactionEntities.map { transactionEntity -> transactionEntity.toModel(
                smsId = transactionEntity.originalSmsId?.toString()
            )  }
            Log.d(TAG, "getAllTransactions: transactionModel: $transactionModels")
            emit(transactionModels)
        }
    }

    override suspend fun updateTransaction(transactionalSms: TransactionalSms): Int {
        return transactionDao.updateTransactionCategory(transactionalSms.id!!, transactionalSms.category.name)
    }
}