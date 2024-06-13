package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke(): List<Transaction>? {
        return transactionEntityRepository.getAllTransactions()
    }
}