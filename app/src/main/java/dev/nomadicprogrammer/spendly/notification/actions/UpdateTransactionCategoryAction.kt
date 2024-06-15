package dev.nomadicprogrammer.spendly.notification.actions

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.TransactionCategory
import dev.nomadicprogrammer.spendly.database.TransactionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateTransactionCategoryAction @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionDao: TransactionDao
) : NotificationAction {
    private val TAG = UpdateTransactionCategoryAction::class.java.simpleName
    companion object{
        const val INTENT_PARAM_TRANSACTION_ID = "transactionId"
        const val INTENT_PARAM_TRANSACTION_CATEGORY = "transactionCategory"
    }

    override operator fun invoke(intent: Intent) {
        val (transactionId, category) = getIntentParams(intent)?:return

        CoroutineScope(Dispatchers.IO).launch {
            updateTransactionAndNotify(transactionId, category, context)
        }
    }

    private suspend fun updateTransactionAndNotify(
        transactionId: String,
        category: String,
        context: Context?
    ) {
        val rowsUpdated = transactionDao.updateTransactionCategory(transactionId, category)
        Log.d(TAG, "Rows updated: $rowsUpdated")
        if (rowsUpdated > 0) {
            Log.d(TAG, "Transaction category updated successfully!")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Transaction category updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getIntentParams(intent: Intent?): Pair<String, String>? {
        val transactionId = intent?.getStringExtra(INTENT_PARAM_TRANSACTION_ID) ?: return null
        val category = intent.getStringExtra(INTENT_PARAM_TRANSACTION_CATEGORY) ?: TransactionCategory.OTHER.name
        Log.d(TAG, "TransactionId: $transactionId, Category: $category")
        return Pair(transactionId, category)
    }
}