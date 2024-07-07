package dev.nomadicprogrammer.spendly.transaction.create.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nomadicprogrammer.spendly.base.TransactionCategoryResourceProvider
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.Categories
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.transaction.create.domain.ValidateCreateTransactionStateUseCase
import dev.nomadicprogrammer.spendly.transaction.create.domain.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
    private val saveTransactionsUseCase: SaveTransactionsUseCase,
    private val amountValidator: Validator,
    private val categoryValidator: Validator,
    private val secondPartyValidator: Validator,
    private val transactionMetadataValidator: Validator,
    private val dateValidator: Validator,
    private val validateCreateTransactionStateUseCase: ValidateCreateTransactionStateUseCase,
    private val transactionCategoryResourceProvider: TransactionCategoryResourceProvider,
    private val categories: Categories
) : ViewModel() {
    private val _state : MutableStateFlow<CreateTransactionState> = MutableStateFlow(
        CreateTransactionState()
    )
    val state : StateFlow<CreateTransactionState> = _state

    private val _toastMessage : MutableSharedFlow<String?> = MutableSharedFlow()
    val toastMessage : SharedFlow<String?> = _toastMessage

    fun onEvent(event: CreateTransactionEvents) {
        when (event) {
            is CreateTransactionEvents.OnTransactionPageLoad -> {
                viewModelScope.launch {
                    val categoriesToLoad = if(event.transactionType == TransactionType.CREDIT) categories.cashInflow else categories.cashOutflow
                    Log.d("CreateTransactionViewModel", "Categories to load: $categoriesToLoad")
                    val categoriesWithResources = categoriesToLoad.map { transactionCategoryResourceProvider.getResource(it) }
                    _state.value = _state.value.copy(categories = categoriesWithResources)
                }
            }

            is CreateTransactionEvents.OnCreateTransactionClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val rowsUpdated = saveTransactionsUseCase(event.transactionalSms)
                    _toastMessage.emit(if(rowsUpdated > 0) "Transaction saved successfully" else "Failed to save transaction")
                }
            }

            is CreateTransactionEvents.ClearToastMessage -> {
                viewModelScope.launch {
                    _toastMessage.emit(null)
                }
            }

            is CreateTransactionEvents.OnTransactionAmountChanged -> {
                Log.d("CreateTransactionViewModel", "Amount changed: ${event.amount}")
                _state.value = _state.value.copy(transactionAmount = event.amount)
                validateState(amountValidator)
            }

            is CreateTransactionEvents.OnTransactionDateSelected -> {
                Log.d("CreateTransactionViewModel", "Date selected: ${event.date}")
                _state.value = _state.value.copy(transactionDate = event.date)
                validateState(dateValidator)
            }

            is CreateTransactionEvents.OnTransactionCategorySelected -> {
                Log.d("CreateTransactionViewModel", "Category selected: ${event.category}")
                _state.value = _state.value.copy(transactionCategory = event.category)
                validateState(categoryValidator)
            }

            is CreateTransactionEvents.OnTransactionMediumSelected -> {
                Log.d("CreateTransactionViewModel", "Transaction medium selected: ${event.transactionMedium}")
                _state.value = _state.value.copy(transactionMedium = event.transactionMedium)
                validateState(transactionMetadataValidator)
            }

            is CreateTransactionEvents.OnSecondPartyChanged -> {
                Log.d("CreateTransactionViewModel", "Second party changed: ${event.secondParty}")
                _state.value = _state.value.copy(secondParty = event.secondParty)
                validateState(secondPartyValidator)
            }
        }
    }

    private fun validateState(validator : Validator) {
        viewModelScope.launch(Dispatchers.Default) {
            validator.validate(_state.value).let {
                _state.value = _state.value.copy(error = Pair(it.errorMessage, it.fieldName), enableCreateTransactionButton =  validateCreateTransactionStateUseCase(_state.value).isValid)
            }
        }
    }
}