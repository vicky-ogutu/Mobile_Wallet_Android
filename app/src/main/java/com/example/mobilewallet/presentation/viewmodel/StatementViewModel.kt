package com.example.mobilewallet.presentation.viewmodel

import com.example.mobilewallet.data.local.repository.WalletRepository


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.models.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatementViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatementUiState>(StatementUiState.Loading)
    val uiState: StateFlow<StatementUiState> = _uiState.asStateFlow()

    fun loadTransactions(customerId: String) {
        viewModelScope.launch {
            val result = repository.getLast100Transactions(customerId)
            _uiState.value = if (result.isSuccess) {
                val transactions = result.getOrNull() ?: emptyList()
                StatementUiState.Success(transactions)
            } else {
                StatementUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load transactions")
            }
        }
    }
}

sealed class StatementUiState {
    object Loading : StatementUiState()
    data class Success(val transactions: List<Transaction>) : StatementUiState()
    data class Error(val message: String) : StatementUiState()
}