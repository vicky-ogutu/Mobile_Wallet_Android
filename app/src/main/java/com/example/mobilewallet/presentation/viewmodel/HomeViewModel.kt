package com.example.mobilewallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilewallet.data.local.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {


    val isLoggedIn = repository.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val customerName = repository.customerName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val customerEmail = repository.customerEmail.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val customerAccount = repository.customerAccount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val customer = repository.customer.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val customerId = repository.customerId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Navigation state
    private val _navigationState = MutableStateFlow<HomeNavigationState>(HomeNavigationState.Login)
    val navigationState: StateFlow<HomeNavigationState> = _navigationState

    init {
        // Check login status and navigate accordingly
        viewModelScope.launch {
            repository.isLoggedIn.collect { isLoggedIn ->
                _navigationState.value = if (isLoggedIn) {
                    HomeNavigationState.Home
                } else {
                    HomeNavigationState.Login
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun navigateToLogin() {
        _navigationState.value = HomeNavigationState.Login
    }

    fun navigateToHome() {
        _navigationState.value = HomeNavigationState.Home
    }
}

sealed class HomeNavigationState {
    object Login : HomeNavigationState()
    object Home : HomeNavigationState()
}