package dev.nomadicprogrammer.spendly.notification.actions

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.base.TransactionCategory
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
        const val INTENT_PARAM_NOTIFICATION_ID = "notificationId"

        fun createIntent(context: Context, transactionId: String, category: String, notificationId : Int): Intent {
            return Intent(context, NotificationActionReceiver::class.java).apply {
                action = Actions.ACTION_UPDATE_TRANSACTION_CATEGORY.actionName
                putExtra(INTENT_PARAM_TRANSACTION_ID, transactionId)
                putExtra(INTENT_PARAM_TRANSACTION_CATEGORY, category)
                putExtra(INTENT_PARAM_NOTIFICATION_ID, notificationId)
            }
        }
    }

    override operator fun invoke(intent: Intent) {
        val (transactionId, category, notificationId) = getIntentParams(intent)?:return

        CoroutineScope(Dispatchers.IO).launch {
            updateTransactionAndNotify(transactionId, category, context)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(notificationId)
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
        }else{
            Log.d(TAG, "Transaction category update failed!")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Transaction category update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getIntentParams(intent: Intent?): Triple<String, String, Int>? {
        val transactionId = intent?.getStringExtra(INTENT_PARAM_TRANSACTION_ID) ?: return null
        val category = intent.getStringExtra(INTENT_PARAM_TRANSACTION_CATEGORY) ?: TransactionCategory.OTHER.name
        val notificationID = intent.getIntExtra(INTENT_PARAM_NOTIFICATION_ID, -1)
        Log.d(TAG, "TransactionId: $transactionId, Category: $category")
        return Triple(transactionId, category, notificationID)
    }
}