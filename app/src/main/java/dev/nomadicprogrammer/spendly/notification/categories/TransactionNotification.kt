package dev.nomadicprogrammer.spendly.notification.categories

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.notification.NotificationChannelManager
import dev.nomadicprogrammer.spendly.notification.actions.Actions
import dev.nomadicprogrammer.spendly.notification.actions.NotificationActionReceiver
import dev.nomadicprogrammer.spendly.notification.actions.UpdateTransactionCategoryAction

 class TransactionNotification(
    val transactionId: String,
    val title: String
) : Notification {

    override fun build(context: Context): android.app.Notification {
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_layout)
        remoteViews.setTextViewText(R.id.notification_title, title)
        val actionIntent = Intent(context, NotificationActionReceiver::class.java)
        actionIntent.setAction(Actions.ACTION_UPDATE_TRANSACTION_CATEGORY.actionName)
        actionIntent.putExtra(UpdateTransactionCategoryAction.INTENT_PARAM_TRANSACTION_ID, transactionId)
        actionIntent.putExtra(UpdateTransactionCategoryAction.INTENT_PARAM_TRANSACTION_CATEGORY, "Groceries")
        val pendingIntent = PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.action_button, pendingIntent)

        return NotificationCompat.Builder(context,
            NotificationChannelManager.TRANSACTION_CHANNEL_ID
        )
            .setSmallIcon(androidx.loader.R.drawable.notification_bg_normal_pressed)
            .setContent(remoteViews)
            .build()
    }

}
