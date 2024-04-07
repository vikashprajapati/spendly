package dev.nomadicprogrammer.spendly.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun getAllTransactions(): List<Transaction>
}