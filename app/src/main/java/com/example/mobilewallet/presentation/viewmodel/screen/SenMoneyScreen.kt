package com.example.mobilewallet.presentation.viewmodel.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

    LaunchedEffect(uiState) {
        if (uiState is SendMoneyViewModel.SendMoneyUiState.Success) {
            coroutineScope.launch {
                delay(2000) // Show success message for 2 seconds
                onSuccess()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Money") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                label = { Text("Account To") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    // Get customer account from preferences
                    viewModel.sendMoney(accountTo, amountValue, "ACT1001")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = accountTo.isNotBlank() && amount.toDoubleOrNull() ?: 0.0 > 0
            ) {
                Text("Send Money")
            }

            if (uiState is SendMoneyViewModel.SendMoneyUiState.Success) {
                AlertDialog(
                    onDismissRequest = { viewModel.resetState() },
                    title = { Text("Success") },
                    text = { Text((uiState as SendMoneyViewModel.SendMoneyUiState.Success).message) },
                    confirmButton = {
                        Button(onClick = { viewModel.resetState() }) {
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

            if (uiState is SendMoneyViewModel.SendMoneyUiState.Error) {
                Text(
                    text = (uiState as SendMoneyViewModel.SendMoneyUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}