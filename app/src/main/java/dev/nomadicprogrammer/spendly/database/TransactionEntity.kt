package dev.nomadicprogrammer.spendly.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    val type: String,
    val transactionDate: String?,
    val bankName: String? = null,
    val amount: Float,
    val currency: String,
    val originalSmsId: Long, // Reference to the original SMS
    val category: String? = null
)
