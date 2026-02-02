package com.example.mobilewallet.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.repository.WalletRepository
import com.example.mobilewallet.work.WorkManagerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalTransactionsViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {

    val localTransactions = repository.getLocalTransactions()

    fun retryTransaction(transactionId: String) {
        viewModelScope.launch {
            repository.retryTransaction(transactionId)
            workManagerHelper.enqueueSyncWork()
        }
    }
}