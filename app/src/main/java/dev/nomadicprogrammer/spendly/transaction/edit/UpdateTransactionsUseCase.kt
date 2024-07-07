package dev.nomadicprogrammer.spendly.transaction.edit

import dev.nomadicprogrammer.spendly.home.data.TransactionRepository
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import javax.inject.Inject

class UpdateTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionalSms : TransactionalSms):Int {
        return transactionEntityRepository.updateTransaction(transactionalSms)
    }
}