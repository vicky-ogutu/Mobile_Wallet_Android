package com.example.mobilewallet.presentation.ui

// Use sealed class for consistent state management
sealed class SendMoneyUiState {
    object Idle : SendMoneyUiState()
    object Loading : SendMoneyUiState()
    data class Success(val message: String) : SendMoneyUiState()
    data class Error(val message: String) : SendMoneyUiState()
}

// Helper extension properties for composables
val SendMoneyUiState.isLoading: Boolean get() = this is SendMoneyUiState.Loading
val SendMoneyUiState.isSuccess: Boolean get() = this is SendMoneyUiState.Success
val SendMoneyUiState.successMessage: String?
    get() = if (this is SendMoneyUiState.Success) this.message else null
val SendMoneyUiState.errorMessage: String?
    get() = if (this is SendMoneyUiState.Error) this.message else null

