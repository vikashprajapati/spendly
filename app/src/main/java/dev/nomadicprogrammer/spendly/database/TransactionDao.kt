package dev.nomadicprogrammer.spendly.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transactionEntity: TransactionEntity)

    @Insert
    suspend fun insertTransactions(transactionEntities: List<TransactionEntity>)

    suspend fun getAllTransactions(): List<TransactionEntity>
}