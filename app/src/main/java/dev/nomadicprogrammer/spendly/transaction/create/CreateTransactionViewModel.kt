package dev.nomadicprogrammer.spendly.transaction.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
    private val saveTransactionsUseCase: SaveTransactionsUseCase
) : ViewModel() {
    private val _state : MutableStateFlow<CreateTransactionState> = MutableStateFlow(CreateTransactionState())
    val state : StateFlow<CreateTransactionState> = _state // why we are using state flow here?, backing field?

    private val _toastMessage : MutableSharedFlow<String?> = MutableSharedFlow()
    val toastMessage : SharedFlow<String?> = _toastMessage

    fun onEvent(event: CreateTransactionEvents) {
        when (event) {
            is CreateTransactionEvents.OnCreateTransactionClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val rowsUpdated = saveTransactionsUseCase(event.transaction)
                    _toastMessage.emit(if(rowsUpdated > 0) "Transaction saved successfully" else "Failed to save transaction")
                }
            }

            is CreateTransactionEvents.ClearToastMessage -> {
                viewModelScope.launch {
                    _toastMessage.emit(null)
                }
            }
        }
    }
}