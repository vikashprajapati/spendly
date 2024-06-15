package dev.nomadicprogrammer.spendly.notification.actions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver(){
    private val TAG : String = NotificationActionReceiver::class.java.simpleName

    @Inject
    lateinit var updateTransactionCategoryAction: NotificationAction

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ${intent?.action}")
        val action = intent?.action?.let { Actions.from(it) }?:return
        if (context != null) {
            when(action){
                Actions.ACTION_UPDATE_TRANSACTION_CATEGORY -> updateTransactionCategoryAction(intent)
            }
        }
    }
}