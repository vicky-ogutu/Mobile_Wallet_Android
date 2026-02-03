package com.example.mobilewallet.presentation.ui

import com.example.mobilewallet.models.Transaction

sealed class StatementUiState {
    object Idle : StatementUiState()
    object Loading : StatementUiState()
    data class Success(val transactions: List<Transaction>) : StatementUiState()
    data class Error(val message: String) : StatementUiState()
}

val StatementUiState.isLoading: Boolean get() = this is StatementUiState.Loading
val StatementUiState.isSuccess: Boolean get() = this is StatementUiState.Success
val StatementUiState.transactionList: List<Transaction>?
    get() = if (this is StatementUiState.Success) this.transactions else null
val StatementUiState.errorMessage: String?
    get() = if (this is StatementUiState.Error) this.message else null