package com.example.mobilewallet.presentation.ui

// Use sealed class for Login state
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

// Helper extension properties for composables
val LoginUiState.isLoading: Boolean get() = this is LoginUiState.Loading
val LoginUiState.isSuccess: Boolean get() = this is LoginUiState.Success
val LoginUiState.errorMessage: String?
    get() = if (this is LoginUiState.Error) this.message else null
