package dev.nomadicprogrammer.spendly.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transactionEntity: TransactionEntity)

    @Insert
    suspend fun insertTransactions(transactionEntities: List<TransactionEntity>)

    @Query("SELECT * FROM TransactionEntity")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("UPDATE TransactionEntity SET category = :category WHERE transactionId = :transactionId")
    suspend fun updateTransactionCategory(transactionId: String, category: String) : Int
}