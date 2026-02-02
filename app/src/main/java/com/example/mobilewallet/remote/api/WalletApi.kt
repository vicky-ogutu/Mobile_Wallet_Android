package com.example.mobilewallet.remote.api

import com.example.mobilewallet.models.Account
import com.example.mobilewallet.models.BalanceRequest
import com.example.mobilewallet.models.Customer
import com.example.mobilewallet.models.LoginRequest
import com.example.mobilewallet.models.SendMoneyRequest
import com.example.mobilewallet.models.Transaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApi {
    @GET("api/v1/customers/")
    suspend fun getCustomers(): List<Customer>

    @POST("api/v1/customers/login")
    suspend fun login(@Body request: LoginRequest): Response<String>

    @GET("api/v1/accounts/")
    suspend fun getAccounts(): List<Account>

    @POST("api/v1/accounts/balance")
    suspend fun getBalance(@Body request: BalanceRequest): BalanceResponse

    @GET("api/v1/transactions/")
    suspend fun getTransactions(): List<Transaction>

    @POST("api/v1/transactions/last-100-transactions")
    suspend fun getLast100Transactions(@Body request: BalanceRequest): List<Transaction>

    @POST("api/v1/transactions/send-money")
    suspend fun sendMoney(@Body request: SendMoneyRequest): Response<String>
}

data class BalanceResponse(
    val balance: Double,
    val accountNo: String
)