package com.example.mobilewallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.repository.WalletRepository
import com.example.mobilewallet.presentation.ui.SendMoneyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SendMoneyUiState>(SendMoneyUiState.Idle)
    val uiState: StateFlow<SendMoneyUiState> = _uiState.asStateFlow()

    fun sendMoney(toAccount: String, amount: Double) {
        if (toAccount.isBlank() || amount <= 0) {
            _uiState.value = SendMoneyUiState.Error("Invalid input data")
            return
        }

        _uiState.value = SendMoneyUiState.Loading
        viewModelScope.launch {
            try {
                // Get current account from repository
                val fromAccount = repository.customerAccount.first()
                if (fromAccount == null) {
                    _uiState.value = SendMoneyUiState.Error("User not logged in or account not found")
                    return@launch
                }

                val result = repository.sendMoney(fromAccount, toAccount, amount)
                _uiState.value = if (result.isSuccess) {
                    SendMoneyUiState.Success("Successfully sent $${String.format("%.2f", amount)} to $toAccount")
                } else {
                    SendMoneyUiState.Error(result.exceptionOrNull()?.message ?: "Transaction failed")
                }
            } catch (e: Exception) {
                _uiState.value = SendMoneyUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetState() {
        _uiState.value = SendMoneyUiState.Idle
    }
}
