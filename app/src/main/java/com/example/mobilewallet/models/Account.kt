package com.example.mobilewallet.models

data class Account(
    val id: Int = 0,
    val accountNo: String = "",
    val customerId: String = "",
    val balance: Double = 0.0
)
