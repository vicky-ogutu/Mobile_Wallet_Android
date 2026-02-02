package com.example.mobilewallet.models


import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val code: Int = 200
)

@Serializable
data class BalanceResponse(
    val balance: Double,
    val accountNo: String,
    val customerName: String? = null
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val customer: Customer? = null,
    val account: Account? = null
)
