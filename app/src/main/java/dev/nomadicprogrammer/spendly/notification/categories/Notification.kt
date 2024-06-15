package dev.nomadicprogrammer.spendly.notification.categories

import android.content.Context
import kotlin.random.Random

sealed class Notification{
    internal val notificationId = Random.nextInt()
    internal abstract fun build(context: Context) : android.app.Notification

    fun show(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(notificationId, build(context))
    }

}
