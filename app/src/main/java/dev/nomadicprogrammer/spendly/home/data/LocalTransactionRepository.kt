package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.database.TransactionDao
import dev.nomadicprogrammer.spendly.home.data.mappers.TransactionMapper
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsDataSource

class LocalTransactionRepository(
    private val transactionDao: TransactionDao,
    private val transactionMapper: TransactionMapper,
    private val smsInbox: SmsDataSource
) : TransactionRepository {
    override suspend fun saveTransaction(transactionalSms: Transaction) {
        val transactionEntity = transactionMapper.toTransactionEntity(transactionalSms)
        transactionDao.insertTransaction(transactionEntity)
    }

    override suspend fun saveTransactions(transactionalSms: List<Transaction>) {
        val transactionEntities = transactionalSms.map { transactionMapper.toTransactionEntity(it) }
        transactionDao.insertTransactions(transactionEntities)
    }

    override suspend fun getTransactions(): List<Transaction>? {
        val transactionEntities = transactionDao.getAllTransactions()
        val smsIds = transactionEntities.map { it.originalSmsId.toInt() }
        val originalSmsList = smsInbox.getSmsByIds(smsIds)
        return originalSmsList?.let { transactionMapper.toTransactions(transactionEntities, it) }
    }
}