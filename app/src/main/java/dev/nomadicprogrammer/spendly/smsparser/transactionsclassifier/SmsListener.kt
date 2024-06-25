package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.nomadicprogrammer.spendly.base.TransactionCategoryProvider
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.notification.categories.TransactionNotification
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class SmsListener : BroadcastReceiver(){
    private val TAG = SmsListener::class.java.simpleName
    @Inject
    lateinit var transactionSmsClassifier: TransactionSmsClassifier

    @Inject
    lateinit var saveTransactionUseCase: SaveTransactionsUseCase
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "SMS Received")
        CoroutineScope(Dispatchers.Default).launch {
            if (context !=null  && intent?.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
                val sms = parseSmsPayload(intent)
                val transaction = transactionSmsClassifier.classify(sms)
                if (transaction != null) {
                    withContext(Dispatchers.IO){
                        val transactionID = saveTransactionUseCase(transaction)
                        val actionCategories = TransactionCategoryProvider.provideCategoriesForNotificationActions()
                        TransactionNotification(
                            transactionId = transactionID.toString(),
                            actionCategories = actionCategories
                        ).show(context)
                    }
                }
            }
        }
    }

    private fun parseSmsPayload(intent: Intent?) : Sms{
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        val message = messages.joinToString(separator = "") { it?.messageBody ?: "" }
        val sender = messages[0]?.originatingAddress!!
        val date = messages[0]?.timestampMillis

        return Sms(
            id = Random.nextInt().toString(),
            senderId = sender,
            msgBody = message,
            date = date ?: System.currentTimeMillis()
        )
    }
}