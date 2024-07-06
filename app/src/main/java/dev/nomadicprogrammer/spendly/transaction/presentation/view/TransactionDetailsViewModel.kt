package dev.nomadicprogrammer.spendly.transaction.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nomadicprogrammer.spendly.home.data.OriginalSmsFetchUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Credit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Debit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val originalSmsFetchUseCase: OriginalSmsFetchUseCase,
) : ViewModel() {
    private val _state : MutableStateFlow<TransactionDetailsState> = MutableStateFlow(TransactionDetailsState())
    val state : StateFlow<TransactionDetailsState> = _state

    fun onEvent(event: TransactionDetailsEvent){
        when(event){
            is TransactionDetailsPageLoad -> {
                _state.value = _state.value.copy(transactionStateHolder = event.transactionStateHolder)

                viewModelScope.launch(Dispatchers.IO){
                    event.transactionStateHolder.transactionalSms.smsId?.let {smsId->
                        val originalSms = originalSmsFetchUseCase(smsId)
                        val transactionalSms = if(event.transactionStateHolder.transactionalSms is Debit){
                            event.transactionStateHolder.transactionalSms.copy(originalSms = originalSms)
                        }else{
                            (event.transactionStateHolder.transactionalSms as Credit).copy(originalSms = originalSms)
                        }
                        _state.value = _state.value.copy(transactionStateHolder = event.transactionStateHolder.copy(transactionalSms = transactionalSms))
                    }
                }
            }
        }
    }
}