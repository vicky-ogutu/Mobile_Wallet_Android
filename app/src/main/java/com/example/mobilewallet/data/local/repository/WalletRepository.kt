package com.example.mobilewallet.data.local.repository


import com.example.mobilewallet.data.local.Dao.LocalTransactionDao
import com.example.mobilewallet.data.local.datastore.PreferencesManager
import com.example.mobilewallet.data.local.entity.LocalTransaction
import com.example.mobilewallet.data.local.entity.SyncStatus
import com.example.mobilewallet.models.BalanceRequest
import com.example.mobilewallet.models.Customer
import com.example.mobilewallet.models.LoginRequest
import com.example.mobilewallet.models.SendMoneyRequest
import com.example.mobilewallet.models.Transaction
import com.example.mobilewallet.remote.api.WalletApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val api: WalletApi,
    private val localTransactionDao: LocalTransactionDao,
    private val preferencesManager: PreferencesManager
) {
    // Remote operations
    suspend fun login(loginRequest: LoginRequest): Result<String> {
        return try {
            val response = api.login(loginRequest)
            if (response.isSuccessful && response.body() == "OK") {
                // Get customer details and account
                val customers = api.getCustomers()
                val customer = customers.find { it.customerId == loginRequest.customerId }
                val accounts = api.getAccounts()
                val account = accounts.find { it.customerId == loginRequest.customerId }

                if (customer != null && account != null) {
                    preferencesManager.saveLogin(customer, account.accountNo)
                    Result.success("Login successful")
                } else {
                    Result.failure(Exception("Customer or account not found"))
                }
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBalance(customerId: String): Result<Double> {
        return try {
            val response = api.getBalance(BalanceRequest(customerId))
            Result.success(response.balance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLast100Transactions(customerId: String): Result<List<Transaction>> {
        return try {
            val transactions = api.getLast100Transactions(BalanceRequest(customerId))
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMoneyRemote(request: SendMoneyRequest): Result<String> {
        return try {
            val response = api.sendMoney(request)
            if (response.isSuccessful) {
                Result.success("Money sent successfully")
            } else {
                Result.failure(Exception("Failed to send money"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Local transaction operations
    suspend fun queueTransaction(transaction: LocalTransaction) {
        localTransactionDao.insert(transaction)
    }

    fun getLocalTransactions(): Flow<List<LocalTransaction>> {
        return localTransactionDao.getAll()
    }

    suspend fun getQueuedTransactions(): List<LocalTransaction> {
        return localTransactionDao.getByStatus(SyncStatus.QUEUED)
    }

    suspend fun updateTransaction(transaction: LocalTransaction) {
        localTransactionDao.update(transaction)
    }

    suspend fun retryTransaction(transactionId: String): LocalTransaction? {
        val transaction = localTransactionDao.getById(transactionId)
        transaction?.let {
            it.syncStatus = SyncStatus.QUEUED
            it.attemptCount += 1
            localTransactionDao.update(it)
        }
        return transaction
    }

    // Session management
    suspend fun logout() {
        preferencesManager.clearLogin()
    }

    val isLoggedIn: Flow<Boolean> = preferencesManager.isLoggedIn
    val customer: Flow<Customer?> = preferencesManager.customer
    val customerName: Flow<String?> = preferencesManager.customerName
    val customerEmail: Flow<String?> = preferencesManager.customerEmail
    val customerAccount: Flow<String?> = preferencesManager.customerAccount
}