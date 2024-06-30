package dev.nomadicprogrammer.spendly.transaction.data

import android.util.Log
import dev.nomadicprogrammer.spendly.transaction.presentation.create.CreateTransactionState

data class UiValidationStatus(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val fieldName: String? = null
)

abstract class Validator{
    abstract fun setNext(next: Validator)
    abstract fun validate(state : CreateTransactionState) : UiValidationStatus
}


class DateValidator : Validator() {
    private var next: Validator? = null

    override fun setNext(next: Validator) {
        this.next = next
    }
    override fun validate(state: CreateTransactionState): UiValidationStatus {
        val isValid = state.transactionDate.isEmpty().not()
        return UiValidationStatus(isValid, if (!isValid) "Enter valid date" else null, "date")
    }
}

class AmountValidator : Validator() {
    private var next: Validator? = null

    override fun setNext(next: Validator) {
        this.next = next
    }
    override fun validate(state: CreateTransactionState): UiValidationStatus {
        if (state.transactionAmount.isEmpty()) {
            return UiValidationStatus(false, "Amount is empty", "amount")
        }

        if (state.transactionAmount.toDoubleOrNull() == null) {
            Log.d("AmountValidator", "Amount is not valid: ${state.transactionAmount}")
            return UiValidationStatus(false, "Enter valid amount", "amount")
        }

        return UiValidationStatus(true)
    }
}

class CategoryValidator(private val categories: List<String>) : Validator() {
    private var next: Validator? = null

    override fun setNext(next: Validator) {
        this.next = next
    }
    override fun validate(state: CreateTransactionState): UiValidationStatus {
        val isValid = state.transactionCategory.isEmpty().not() && categories.contains(state.transactionCategory)
        return UiValidationStatus(isValid, if (!isValid) "Enter valid category" else null, "category")
    }
}

class TransactionMetadataValidator(
    private val transactionMedium : List<String>
) : Validator() {
    private var next: Validator? = null

    override fun setNext(next: Validator) {
        this.next = next
    }
    override fun validate(state: CreateTransactionState): UiValidationStatus {
        val isValid = state.transactionMedium .isEmpty().not() && transactionMedium.contains(state.transactionMedium)
        return UiValidationStatus(isValid, if (!isValid) "Enter valid transaction medium" else null, "transaction medium")
    }
}

class SecondPartyValidator : Validator() {
    private var next: Validator? = null

    override fun setNext(next: Validator) {
        this.next = next
    }
    override fun validate(state: CreateTransactionState): UiValidationStatus {
        val isValid = state.secondParty.isNotEmpty()
        return UiValidationStatus(isValid, if (!isValid) "Enter valid name" else null, "second party")
    }
}