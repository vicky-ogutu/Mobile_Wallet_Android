package com.example.mobilewallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.entity.LocalTransaction
import com.example.mobilewallet.data.local.entity.SyncStatus
import com.example.mobilewallet.data.local.repository.WalletRepository
import com.example.mobilewallet.work.WorkManagerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<SendMoneyUiState>(SendMoneyUiState.Idle)
    val uiState: StateFlow<SendMoneyUiState> = _uiState.asStateFlow()

    fun sendMoney(accountTo: String, amount: Double, customerAccount: String) {
        if (accountTo.isBlank() || amount <= 0) {
            _uiState.value = SendMoneyUiState.Error("Please enter valid account and amount")
            return
        }

        viewModelScope.launch {
            // Create local transaction
            val transaction = LocalTransaction(
                accountFrom = customerAccount,
                accountTo = accountTo,
                amount = amount,
                createdAt = Date(),
                syncStatus = SyncStatus.QUEUED
            )

            // Save to local database
            repository.queueTransaction(transaction)

            // Enqueue work for sync
            workManagerHelper.enqueueSyncWork()

            _uiState.value = SendMoneyUiState.Success("Transaction queued for sync")
        }
    }

    fun resetState() {
        _uiState.value = SendMoneyUiState.Idle
    }
}

sealed class SendMoneyUiState {
    object Idle : SendMoneyUiState()
    object Loading : SendMoneyUiState()
    data class Success(val message: String) : SendMoneyUiState()
    data class Error(val message: String) : SendMoneyUiState()
}