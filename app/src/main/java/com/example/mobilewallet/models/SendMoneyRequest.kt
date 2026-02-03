package com.example.mobilewallet.models

data class SendMoneyRequest(
    //val customerId: String,
    val clientTransactionId:String,
    val accountFrom: String,
    val accountTo: String,
    val amount: Double
)
