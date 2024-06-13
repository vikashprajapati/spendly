package dev.nomadicprogrammer.spendly.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.nomadicprogrammer.spendly.database.AppDatabase
import dev.nomadicprogrammer.spendly.database.TransactionDao
import dev.nomadicprogrammer.spendly.home.data.GetAllTransactionsUseCase
import dev.nomadicprogrammer.spendly.home.data.LocalTransactionRepository
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.home.data.TransactionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesSaveTransactionUseCase(transactionRepository: TransactionRepository) : SaveTransactionsUseCase {
        return SaveTransactionsUseCase(transactionRepository)
    }

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext context : Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun providesTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }

    @Singleton
    @Provides
    fun getAllTransactionUseCase(transactionRepository: TransactionRepository) : GetAllTransactionsUseCase{
        return GetAllTransactionsUseCase(transactionRepository)
    }
}