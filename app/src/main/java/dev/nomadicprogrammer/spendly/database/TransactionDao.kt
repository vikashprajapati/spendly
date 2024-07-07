package dev.nomadicprogrammer.spendly.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transactionEntity: TransactionEntity) : Long

    @Insert
    suspend fun insertTransactions(transactionEntities: List<TransactionEntity>)

    @Query("SELECT * FROM TransactionEntity")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("UPDATE TransactionEntity SET category = :category WHERE transactionId = :transactionId")
    suspend fun updateTransactionCategory(transactionId: String, category: String) : Int

    @Delete
    suspend fun deleteTransaction(transactionEntity: TransactionEntity) : Int
}