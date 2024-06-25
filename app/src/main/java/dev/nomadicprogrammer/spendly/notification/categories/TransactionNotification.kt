package dev.nomadicprogrammer.spendly.notification.categories

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import dev.nomadicprogrammer.spendly.MainActivity
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.notification.NotificationChannelManager
import dev.nomadicprogrammer.spendly.notification.actions.UpdateTransactionCategoryAction
import java.util.Random

class TransactionNotification(
    val transactionId: String,
    val title: String = "Transaction Detected",
    val actionCategories: List<String>
) : Notification() {
    override fun build(context: Context): android.app.Notification {
        val actions = actionCategories.map { createAction(context, it) }
        val mainIntent = PendingIntent.getActivity(
            context,
            Random().nextInt(),
            Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK },
            PendingIntent.FLAG_IMMUTABLE
        )
        return actions.fold(
            NotificationCompat
                .Builder(context, NotificationChannelManager.TRANSACTION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText("Please tag the transaction category.")
                .setContentIntent(mainIntent)
        ) { builder, action -> builder.addAction(action) }
            .build()
    }

    private fun createAction(context: Context, category : String): Action {
        val actionIntent = UpdateTransactionCategoryAction.createIntent(context, transactionId, category, notificationId)
        val pendingIntent = PendingIntent.getBroadcast(context, Random().nextInt(), actionIntent, PendingIntent.FLAG_IMMUTABLE)
        return Action.Builder(null, category, pendingIntent).build()
    }

}
