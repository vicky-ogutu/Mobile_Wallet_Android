package com.example.mobilewallet.presentation.viewmodel.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilewallet.data.local.entity.LocalTransaction
import com.example.mobilewallet.data.local.entity.SyncStatus
import com.example.mobilewallet.presentation.viewmodel.LocalTransactionsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.hilt.navigation.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalTransactionsScreen(
    onBack: () -> Unit,
    viewModel: LocalTransactionsViewModel = hiltViewModel()
) {
    val transactions by viewModel.localTransactions.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local Transactions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (transactions.any { it.syncStatus == SyncStatus.FAILED }) {
                FloatingActionButton(
                    onClick = { /* Sync all failed transactions */ },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "Sync All")
                }
            }
        }
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No local transactions")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(transactions) { transaction ->
                    LocalTransactionItem(
                        transaction = transaction,
                        onRetry = { viewModel.retryTransaction(transaction.clientTransactionId) }
                    )
                }
            }
        }
    }
}

@Composable
fun LocalTransactionItem(
    transaction: LocalTransaction,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "To: ${transaction.accountTo}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Amount: $${String.format("%.2f", transaction.amount)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                            .format(transaction.createdAt),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                SyncStatusBadge(status = transaction.syncStatus)
            }

            transaction.lastError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Error: $error",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (transaction.syncStatus == SyncStatus.FAILED) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun SyncStatusBadge(status: SyncStatus) {
    val (text, color) = when (status) {
        SyncStatus.QUEUED -> "Queued" to Color(0xFFFB8C00) // Orange
        SyncStatus.SYNCING -> "Syncing" to Color(0xFF1976D2) // Blue
        SyncStatus.SYNCED -> "Synced" to Color(0xFF388E3C) // Green
        SyncStatus.FAILED -> "Failed" to Color(0xFFD32F2F) // Red
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}