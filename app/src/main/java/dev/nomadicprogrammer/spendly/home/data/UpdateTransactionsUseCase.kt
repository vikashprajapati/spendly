package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import javax.inject.Inject

class UpdateTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction : Transaction):Int {
        return transactionEntityRepository.updateTransaction(transaction)
    }
}