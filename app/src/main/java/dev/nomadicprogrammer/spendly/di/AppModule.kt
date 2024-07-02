package dev.nomadicprogrammer.spendly.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.nomadicprogrammer.spendly.base.TransactionCategoryResource
import dev.nomadicprogrammer.spendly.base.TransactionCategoryResourceProvider
import dev.nomadicprogrammer.spendly.database.AppDatabase
import dev.nomadicprogrammer.spendly.database.TransactionCategoryConverter
import dev.nomadicprogrammer.spendly.database.TransactionDao
import dev.nomadicprogrammer.spendly.home.data.GetAllTransactionsUseCase
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.home.data.TransactionRepository
import dev.nomadicprogrammer.spendly.home.data.UpdateTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.BundledCategories
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.Categories
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.TransactionCategory
import dev.nomadicprogrammer.spendly.transaction.data.AmountValidator
import dev.nomadicprogrammer.spendly.transaction.data.CategoryValidator
import dev.nomadicprogrammer.spendly.transaction.data.DateValidator
import dev.nomadicprogrammer.spendly.transaction.data.SecondPartyValidator
import dev.nomadicprogrammer.spendly.transaction.data.TransactionMetadataValidator
import dev.nomadicprogrammer.spendly.transaction.data.ValidateCreateTransactionStateUseCase
import dev.nomadicprogrammer.spendly.transaction.data.Validator
import javax.inject.Named
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
    fun providesAppDatabase(
        @ApplicationContext context : Context,
        transactionCategoryConverter: TransactionCategoryConverter
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).addTypeConverter(transactionCategoryConverter)
            .build()
    }

    @Singleton
    @Provides
    fun providesTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }

    @Singleton
    @Provides
    fun getAllTransactionUseCase(@ApplicationContext context: Context, transactionRepository: TransactionRepository) : GetAllTransactionsUseCase{
        return GetAllTransactionsUseCase(transactionRepository, TransactionCategoryResourceProvider(context))
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

    @Singleton
    @Provides
    fun providesCategories(@ApplicationContext context: Context) : Categories{
        return BundledCategories(context, "categories.json").parse()
    }

    @Provides
    @Named("notificationActionCategories")
    fun provideCategoriesForNotificationActions(
        categories: Categories,
        transactionCategoryResourceProvider: TransactionCategoryResourceProvider
    ): List<Pair<String, TransactionCategoryResource>> {
        return (categories.cashOutflow + categories.cashInflow)
            .shuffled()
            .take(3)
            .map { Pair(it.name, transactionCategoryResourceProvider.getResource(it)) }
    // TODO: Enhance to use a more sophisticated algorithm
    }

    @Provides
    @Singleton
    fun provideTransactionCategoryResources(@ApplicationContext context: Context) : TransactionCategoryResourceProvider {
        return TransactionCategoryResourceProvider(context)
    }

    @Provides
    @Singleton
    fun provideCashOutflowCategories(categories: Categories): List<TransactionCategory.CashOutflow> {
        return categories.cashOutflow
    }

    @Provides
    @Singleton
    fun provideCashInflowCategories(categories: Categories): List<TransactionCategory.CashInflow> {
        return categories.cashInflow
    }

    @Singleton
    @Provides
    fun provideAllCategoryResources(
        categories: Categories,
        transactionCategoryResourceProvider: TransactionCategoryResourceProvider
    ): List<TransactionCategoryResource> {
        return (categories.cashOutflow + categories.cashInflow)
            .map { transactionCategoryResourceProvider.getResource(it) }
    }
}