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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilewallet.presentation.viewmodel.HomeViewModel
//import com.wallet.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCheckBalance: () -> Unit,
    onSendMoney: () -> Unit,
    onProfile: () -> Unit,
    onStatement: () -> Unit,
    onLocalTransactions: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val customerName by viewModel.customerName.collectAsState(initial = null)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mobile Wallet") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
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
            // Welcome header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WELCOME",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = customerName ?: "Customer",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Actions grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(homeActions) { action ->
                    ActionCard(
                        title = action.title,
                        icon = action.icon,
                        onClick = action.onClick
                    )
                }
            }
        }
    }
}

data class HomeAction(
    val title: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit
)

val homeActions = listOf(
    HomeAction(
        title = "Check Balance",
        icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) }
    ) { /* Will be set by caller */ },
    HomeAction(
        title = "Send Money",
        icon = { Icon(Icons.Default.Send, contentDescription = null) }
    ) { /* Will be set by caller */ },
    HomeAction(
        title = "View Profile",
        icon = { Icon(Icons.Default.Person, contentDescription = null) }
    ) { /* Will be set by caller */ },
    HomeAction(
        title = "View Statement",
        icon = { Icon(Icons.Default.Receipt, contentDescription = null) }
    ) { /* Will be set by caller */ },
    HomeAction(
        title = "Local Transactions",
        icon = { Icon(Icons.Default.History, contentDescription = null) }
    ) { /* Will be set by caller */ }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            icon()
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}