package dev.nomadicprogrammer.spendly.notification.actions

import android.content.Intent

interface NotificationAction {
    operator fun invoke(intent: Intent)
}

enum class Actions(val actionName: String) {
    ACTION_UPDATE_TRANSACTION_CATEGORY("UPDATE_TRANSACTION_CATEGORY");

    companion object {
        fun from(actionName: String): Actions? {
            return entries.find { it.actionName == actionName }
        }
    }
}