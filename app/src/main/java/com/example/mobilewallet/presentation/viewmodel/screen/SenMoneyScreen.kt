package com.example.mobilewallet.presentation.viewmodel.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilewallet.presentation.ui.SendMoneyUiState
import com.example.mobilewallet.presentation.viewmodel.SendMoneyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: SendMoneyViewModel = hiltViewModel()
) {
    var accountTo by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Reset state when entering screen
    DisposableEffect(Unit) {
        viewModel.resetState()
        onDispose {
            // Optional cleanup
        }
    }

    // Handle success state
    LaunchedEffect(uiState) {
        if (uiState is SendMoneyUiState.Success) {
            coroutineScope.launch {
                delay(2000) // Show success message for 2 seconds
                onSuccess()
                viewModel.resetState()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Money") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetState()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = accountTo,
                onValueChange = { accountTo = it },
                label = { Text("Recipient Account Number") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is SendMoneyUiState.Loading
            )

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    if (it.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                        amount = it
                    }
                },
                label = { Text("Amount") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = uiState !is SendMoneyUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    // In a real app, get fromAccount from shared preferences or secure storage
                    viewModel.sendMoney(accountTo, amountValue)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = accountTo.isNotBlank() &&
                        amount.toDoubleOrNull() ?: 0.0 > 0 &&
                        uiState !is SendMoneyUiState.Loading &&
                        uiState !is SendMoneyUiState.Success
            ) {
                if (uiState is SendMoneyUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...")
                } else {
                    Text("Send Money")
                }
            }

            // Handle different UI states
            when (uiState) {
                is SendMoneyUiState.Success -> {
                    AlertDialog(
                        onDismissRequest = { /* Don't allow dismiss by clicking outside */ },
                        title = { Text("Success") },
                        text = {
                            Text(
                                (uiState as SendMoneyUiState.Success).message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.resetState()
                                    onSuccess()
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
                is SendMoneyUiState.Error -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (uiState as SendMoneyUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.resetState() },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Try Again")
                    }
                }
                else -> {
                    // Idle or Loading states - nothing extra to show
                }
            }
        }
    }
}