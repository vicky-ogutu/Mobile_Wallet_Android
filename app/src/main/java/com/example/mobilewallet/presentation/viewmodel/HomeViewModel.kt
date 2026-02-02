package com.example.mobilewallet.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.repository.WalletRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    val customerName: StateFlow<String?> = repository.customerName as StateFlow<String?>
    val customerEmail: StateFlow<String?> = repository.customerEmail as StateFlow<String?>
    val customerAccount: StateFlow<String?> = repository.customerAccount as StateFlow<String?>

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}