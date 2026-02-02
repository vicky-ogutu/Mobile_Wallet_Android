package com.example.mobilewallet.presentation.viewmodel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilewallet.presentation.viewmodel.HomeViewModel
import com.example.mobilewallet.presentation.viewmodel.screen.HomeScreen
import com.example.mobilewallet.presentation.viewmodel.screen.LoginScreen


@Composable
fun WalletNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val customerName by homeViewModel.customerName.collectAsState(initial = null)

    LaunchedEffect(customerName) {
        if (customerName != null && startDestination == Screen.Login.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCheckBalance = { navController.navigate(Screen.Balance.route) },
                onSendMoney = { navController.navigate(Screen.SendMoney.route) },
                onProfile = { navController.navigate(Screen.Profile.route) },
                onStatement = { navController.navigate(Screen.Statement.route) },
                onLocalTransactions = { navController.navigate(Screen.LocalTransactions.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Balance.route) {
            BalanceScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SendMoney.route) {
            SendMoneyScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Statement.route) {
            StatementScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LocalTransactions.route) {
            LocalTransactionsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Balance : Screen("balance")
    object SendMoney : Screen("send_money")
    object Profile : Screen("profile")
    object Statement : Screen("statement")
    object LocalTransactions : Screen("local_transactions")
}