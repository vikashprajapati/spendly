package dev.nomadicprogrammer.spendly.transaction.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nomadicprogrammer.spendly.home.data.OriginalSmsFetchUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Credit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Debit
import dev.nomadicprogrammer.spendly.transaction.domain.TransactionDeleteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val originalSmsFetchUseCase: OriginalSmsFetchUseCase,
    private val deleteTransactionUseCase: TransactionDeleteUseCase
) : ViewModel() {
    private val _state : MutableStateFlow<TransactionDetailsState> = MutableStateFlow(TransactionDetailsState())
    val state : StateFlow<TransactionDetailsState> = _state

    private val _toastMessage = MutableSharedFlow<String?>()
    val toastMessage : SharedFlow<String?> = _toastMessage

    fun onEvent(event: TransactionDetailsEvent){
        when(event){
            is TransactionDetailsPageLoad -> {
                viewModelScope.launch(Dispatchers.IO){
                    event.originalSmsId?.let {smsId->
                        val originalSms = originalSmsFetchUseCase(smsId)
                        _state.value = _state.value.copy(originalSms = originalSms)
                    }
                }
            }

            is OnDeleteClicked -> {
                viewModelScope.launch(Dispatchers.IO){
                    val deleted = deleteTransactionUseCase(event.transactionalSms)
                    if(deleted > 0){
                        _state.value = _state.value.copy(transactionDeleted = true)
                        _toastMessage.emit("Transaction deleted")
                    }else{
                        _toastMessage.emit("Failed to delete transaction")
                    }
                }
            }

            is ClearToast -> {
                viewModelScope.launch {
                    _toastMessage.emit(null)
                }
            }
        }
    }
}