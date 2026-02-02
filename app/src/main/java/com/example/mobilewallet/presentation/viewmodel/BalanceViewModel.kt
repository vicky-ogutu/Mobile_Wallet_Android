package com.example.mobilewallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BalanceUiState>(BalanceUiState.Idle)
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    fun getBalance(customerId: String) {
        _uiState.value = BalanceUiState.Loading
        viewModelScope.launch {
            val result = repository.getBalance(customerId)
            _uiState.value = if (result.isSuccess) {
                BalanceUiState.Success(result.getOrNull() ?: 0.0)
            } else {
                BalanceUiState.Error(result.exceptionOrNull()?.message ?: "Failed to get balance")
            }
        }
    }

    fun resetState() {
        _uiState.value = BalanceUiState.Idle
    }
}

sealed class BalanceUiState {
    object Idle : BalanceUiState()
    object Loading : BalanceUiState()
    data class Success(val balance: Double) : BalanceUiState()
    data class Error(val message: String) : BalanceUiState()
}