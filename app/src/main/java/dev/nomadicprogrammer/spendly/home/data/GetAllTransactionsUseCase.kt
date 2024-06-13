package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke():Flow<List<Transaction>?> {
        return transactionEntityRepository.getAllTransactions()
    }
}