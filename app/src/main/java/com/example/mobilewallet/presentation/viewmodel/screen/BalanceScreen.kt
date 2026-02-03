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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilewallet.presentation.ui.BalanceUiState
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
        viewModel.getBalance("CUST1001")
        showDialog = true
    }

    // Reset state when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = {
            coroutineScope.launch {
                delay(300)
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
                        is BalanceUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is BalanceUiState.Error -> {
                            Text(
                                text = (uiState as BalanceUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        is BalanceUiState.Success -> {
                            Text(
                                text = "$${String.format("%.2f", (uiState as BalanceUiState.Success).balance)}",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        else -> {
                            // Idle state - show nothing or a placeholder
                            Text("Ready to fetch balance...")
                        }
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