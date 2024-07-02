package dev.nomadicprogrammer.spendly.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [TransactionEntity::class], version = 1)
@TypeConverters(TransactionCategoryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "SpendlyDB"
    }
}