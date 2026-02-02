package com.example.mobilewallet.presentation.viewmodel.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilewallet.models.Transaction
import com.example.mobilewallet.presentation.viewmodel.StatementUiState
import com.example.mobilewallet.presentation.viewmodel.StatementViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementScreen(
    onBack: () -> Unit,
    viewModel: StatementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTransactions("CUST1001") // Get from preferences
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statement") },
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
        ) {
            when (uiState) {
                is StatementViewModel.StatementUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is StatementViewModel.StatementUiState.Success -> {
                    val transactions = (uiState as StatementViewModel.StatementUiState.Success).transactions

                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions found")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            items(transactions) { transaction ->
                                TransactionItem(transaction = transaction)
                            }
                        }

                        // Total summary
                        val total = transactions.sumOf { it.amount }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Amount:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format("%.2f", total)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (total >= 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                is StatementViewModel.StatementUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (uiState as StatementViewModel.StatementUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is StatementUiState.Error -> TODO()
                StatementUiState.Loading -> TODO()
                is StatementUiState.Success -> TODO()
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        .format(transaction.createdAt),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = transaction.transactionType,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${if (transaction.isCredit) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.isCredit) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}