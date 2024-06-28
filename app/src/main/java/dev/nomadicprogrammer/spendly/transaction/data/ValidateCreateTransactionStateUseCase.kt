package dev.nomadicprogrammer.spendly.transaction.data

import dev.nomadicprogrammer.spendly.transaction.create.CreateTransactionState
import javax.inject.Inject

class ValidateCreateTransactionStateUseCase @Inject constructor(
    private val validator: Validator
) {
    operator  fun invoke(createTransactionState: CreateTransactionState) : UiValidationStatus {
        return validator.validate(createTransactionState)
    }
}