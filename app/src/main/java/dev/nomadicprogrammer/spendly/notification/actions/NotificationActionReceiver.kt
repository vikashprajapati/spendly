package dev.nomadicprogrammer.spendly.notification.actions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.nomadicprogrammer.spendly.notification.actions.UpdateTransactionCategoryAction.Companion.ACTION_UPDATE_TRANSACTION_CATEGORY
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver(){
    private val TAG : String = NotificationActionReceiver::class.java.simpleName

    @Inject
    lateinit var updateTransactionCategoryAction: NotificationAction

    private val actions  by lazy { mapOf(ACTION_UPDATE_TRANSACTION_CATEGORY to updateTransactionCategoryAction) }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ${intent?.action}")
        val action = intent?.action?:return
        if (context != null) {
            actions[action]?.invoke(intent)
        }
    }
}