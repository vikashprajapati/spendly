package dev.nomadicprogrammer.spendly.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.startup.Initializer

class NotificationChannelManager : Initializer<Unit> {
    companion object{
        const val TRANSACTION_CHANNEL_ID = "transaction_channel_id"
        const val TRANSACTION_CHANNEL_NAME = "Transaction Alerts Channel"
        const val GENERAL_CHANNEL_ID = "general_channel_id"
        const val GENERAL_CHANNEL_NAME = "General Alerts Channel"
    }
    private lateinit var notificationManager: NotificationManager

    fun createChannels(context: Context) {
        notificationManager = context.getSystemService(NotificationManager::class.java)
        createTransactionChannel()
        createGeneralChannel()
    }

    private fun createGeneralChannel() {
        if (notificationManager.getNotificationChannel(GENERAL_CHANNEL_ID) == null) {
            val generalChannel = NotificationChannel(
                GENERAL_CHANNEL_ID,
                GENERAL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This channel is used to show general alerts"
            }
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    private fun createTransactionChannel() {
        if (notificationManager.getNotificationChannel(TRANSACTION_CHANNEL_ID) == null) {
            val transactionChannel = NotificationChannel(
                TRANSACTION_CHANNEL_ID,
                TRANSACTION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "This channel is used to show transaction alerts"
            }
            notificationManager.createNotificationChannel(transactionChannel)
        }
    }

    override fun create(context: Context) {
        createChannels(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}