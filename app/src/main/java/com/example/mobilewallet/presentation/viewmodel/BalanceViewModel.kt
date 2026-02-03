package com.example.mobilewallet.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.repository.WalletRepository
import com.example.mobilewallet.presentation.ui.BalanceUiState
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
            try {
                val result = repository.getBalance(customerId)
                _uiState.value = if (result.isSuccess) {
                    BalanceUiState.Success(result.getOrNull() ?: 0.0)
                } else {
                    BalanceUiState.Error(result.exceptionOrNull()?.message ?: "Failed to get balance")
                }
            } catch (e: Exception) {
                _uiState.value = BalanceUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = BalanceUiState.Idle
    }
}