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
import dev.nomadicprogrammer.spendly.home.data.UpdateTransactionsUseCase
import dev.nomadicprogrammer.spendly.transaction.data.AmountValidator
import dev.nomadicprogrammer.spendly.transaction.data.CategoryValidator
import dev.nomadicprogrammer.spendly.transaction.data.DateValidator
import dev.nomadicprogrammer.spendly.transaction.data.SecondPartyValidator
import dev.nomadicprogrammer.spendly.transaction.data.TransactionMetadataValidator
import dev.nomadicprogrammer.spendly.transaction.data.ValidateCreateTransactionStateUseCase
import dev.nomadicprogrammer.spendly.transaction.data.Validator
import javax.inject.Qualifier
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

    @Singleton
    @Provides
    fun updateTransactionUseCase(transactionRepository: TransactionRepository) : UpdateTransactionsUseCase{
        return UpdateTransactionsUseCase(transactionRepository)
    }

    @Singleton
    @Provides
    fun provideValidateCreateTransactionStateUseCase(validator: Validator) : ValidateCreateTransactionStateUseCase {
        return ValidateCreateTransactionStateUseCase(validator)
    }


    @Singleton
    @Provides
    fun providesNewTransactionDetailsValidator() : Validator {
        return AmountValidator().apply {
            setNext(
                CategoryValidator(categories = listOf("Food", "Grocery", "Fuel", "Rent", "EMI", "Others")).apply {
                    setNext(DateValidator().apply {
                        setNext(
                            TransactionMetadataValidator(
                                listOf("Cash", "Bank", "UPI", "Card")
                            ).apply {
                                setNext(SecondPartyValidator())
                            }
                        )
                    })
                }
            )
        }
    }
}