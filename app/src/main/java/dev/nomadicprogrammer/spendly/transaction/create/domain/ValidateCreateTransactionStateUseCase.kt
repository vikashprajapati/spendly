package dev.nomadicprogrammer.spendly.transaction.create.domain

import dev.nomadicprogrammer.spendly.transaction.create.presentation.CreateTransactionState
import javax.inject.Inject

class ValidateCreateTransactionStateUseCase @Inject constructor(
    private val validator: Validator
) {
    operator  fun invoke(createTransactionState: CreateTransactionState) : UiValidationStatus {
        return validator.validate(createTransactionState)
    }
}