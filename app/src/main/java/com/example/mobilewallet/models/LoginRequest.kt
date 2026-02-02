package com.example.mobilewallet.models


data class LoginRequest(
    val customerId: String,
    val pin: String
)
