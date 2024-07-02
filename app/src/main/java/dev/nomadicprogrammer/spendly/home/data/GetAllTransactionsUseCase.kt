package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.base.TransactionCategoryResourceProvider
import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository,
    private val transactionCategoryResourceProvider: TransactionCategoryResourceProvider
) {
    suspend operator fun invoke():Flow<List<TransactionStateHolder>?>  = flow{
        transactionEntityRepository.getAllTransactions().collect{
            val  transactionStateHolders = it?.map { transactionalSms ->
                TransactionStateHolder(
                    transactionalSms = transactionalSms,
                    transactionCategoryResource = transactionCategoryResourceProvider.getResource(transactionalSms.category)
                )
            }

            emit(transactionStateHolders)
        }

    }
}