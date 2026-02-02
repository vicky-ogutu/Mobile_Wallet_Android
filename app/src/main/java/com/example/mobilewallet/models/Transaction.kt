package com.example.mobilewallet.models

import java.util.Date

data class Transaction(
    val id: Int = 0,
    val transactionId: String = "",
    val accountNo: String = "",
    val amount: Double = 0.0,
    val balance: Double = 0.0,
    val transactionType: String = "",
    val createdAt: Date = Date()
) {
    val isCredit: Boolean
        get() = amount > 0
}
