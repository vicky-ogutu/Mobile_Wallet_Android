package com.example.mobilewallet.models

data class Customer(
    val id: Int = 0,
    val pin: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val customerId: String = ""
) {
    val fullName: String
        get() = "$firstName $lastName"
}
