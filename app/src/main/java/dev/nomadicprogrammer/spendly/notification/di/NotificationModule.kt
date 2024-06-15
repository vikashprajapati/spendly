package dev.nomadicprogrammer.spendly.notification.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.nomadicprogrammer.spendly.notification.actions.NotificationAction
import dev.nomadicprogrammer.spendly.notification.actions.UpdateTransactionCategoryAction

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    abstract fun bindUpdateTransactionCategoryAction(updateTransactionCategoryAction: UpdateTransactionCategoryAction): NotificationAction
}