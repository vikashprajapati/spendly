package dev.nomadicprogrammer.spendly.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController

class HomeViewModelFactory(
    private val spendAnalyserController: SpendAnalyserController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(spendAnalyserController) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}