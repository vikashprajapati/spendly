package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.base.TransactionCategoryResources
import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository,
    private val transactionCategoryResources: TransactionCategoryResources
) {
    suspend operator fun invoke():Flow<List<TransactionStateHolder>?> {
        val transactionalSmsList = transactionEntityRepository.getAllTransactions()
        val  transactionStateHolders = transactionalSmsList.first()?.map { transactionalSms ->
            TransactionStateHolder(
                transactionalSms = transactionalSms,
                transactionCategoryResource = transactionCategoryResources.getResource(transactionalSms.category)
            )
        }

        return flow { emit(transactionStateHolders) }
    }
}