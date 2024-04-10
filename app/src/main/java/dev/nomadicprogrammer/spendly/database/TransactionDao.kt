package dev.nomadicprogrammer.spendly.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transactionEntity: TransactionEntity)

    @Insert
    suspend fun insertTransactions(transactionEntities: List<TransactionEntity>)

    @Query("SELECT * FROM TransactionEntity")
    suspend fun getAllTransactions(): List<TransactionEntity>
}