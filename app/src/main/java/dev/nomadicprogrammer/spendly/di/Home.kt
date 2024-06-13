package dev.nomadicprogrammer.spendly.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.nomadicprogrammer.spendly.home.data.LocalTransactionRepository
import dev.nomadicprogrammer.spendly.home.data.TransactionRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {
    @Binds
    abstract fun localTransactionRepository(localTransactionRepository: LocalTransactionRepository): TransactionRepository
}