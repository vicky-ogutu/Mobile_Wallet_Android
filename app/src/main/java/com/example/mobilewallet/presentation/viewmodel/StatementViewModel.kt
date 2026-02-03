package com.example.mobilewallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.repository.WalletRepository
import com.example.mobilewallet.presentation.ui.StatementUiState
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

    private val _uiState = MutableStateFlow<StatementUiState>(StatementUiState.Idle)
    val uiState: StateFlow<StatementUiState> = _uiState.asStateFlow()

    fun loadTransactions(customerId: String) {
        if (customerId.isBlank()) {
            _uiState.value = StatementUiState.Error("Customer ID is required")
            return
        }

        _uiState.value = StatementUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.getTransactions(customerId)
                _uiState.value = if (result.isSuccess) {
                    StatementUiState.Success(result.getOrNull() ?: emptyList())
                } else {
                    StatementUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load transactions")
                }
            } catch (e: Exception) {
                _uiState.value = StatementUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetState() {
        _uiState.value = StatementUiState.Idle
    }
}