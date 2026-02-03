package com.example.mobilewallet.presentation.ui

import com.example.mobilewallet.presentation.viewmodel.BalanceViewModel

sealed class BalanceUiState {
    object Idle : BalanceUiState()
    object Loading : BalanceUiState()
    data class Success(val balance: Double) : BalanceUiState()
    data class Error(val message: String) : BalanceUiState()
}

val BalanceUiState.isLoading: Boolean get() = this is BalanceUiState.Loading
val BalanceUiState.balance: Double?
    get() = if (this is BalanceUiState.Success) this.balance else null
val BalanceUiState.error: String?
    get() = if (this is BalanceUiState.Error) this.message else null

