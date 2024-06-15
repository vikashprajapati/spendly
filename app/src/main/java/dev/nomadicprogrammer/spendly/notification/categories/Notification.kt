package dev.nomadicprogrammer.spendly.notification.categories

import android.content.Context

sealed interface Notification{
    fun build(context: Context) : android.app.Notification
}
