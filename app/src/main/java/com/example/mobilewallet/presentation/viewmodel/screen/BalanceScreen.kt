package com.example.mobilewallet.presentation.viewmodel.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

import com.example.mobilewallet.presentation.viewmodel.BalanceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BalanceScreen(
    onBack: () -> Unit,
    viewModel: BalanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Get customer ID from preferences or pass it
        // For now, we'll use a placeholder
        viewModel.getBalance("CUST1001")
        showDialog = true
    }

    if (showDialog) {
        Dialog(onDismissRequest = {
            coroutineScope.launch {
                delay(300) // Small delay for smooth transition
                onBack()
            }
        }) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Account Balance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = {
                            showDialog = false
                            coroutineScope.launch {
                                delay(300)
                                onBack()
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    when (uiState) {
                        is BalanceViewModel.BalanceUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is BalanceViewModel.BalanceUiState.Success -> {
                            val balance = (uiState as BalanceViewModel.BalanceUiState.Success).balance
                            Text(
                                text = "$${String.format("%.2f", balance)}",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        is BalanceViewModel.BalanceUiState.Error -> {
                            Text(
                                text = (uiState as BalanceViewModel.BalanceUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {}
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            showDialog = false
                            coroutineScope.launch {
                                delay(300)
                                onBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}