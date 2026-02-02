package com.example.mobilewallet.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val statusCode: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class NetworkError {
    data class HttpError(val code: Int, val message: String) : NetworkError()
    data class ConnectionError(val message: String) : NetworkError()
    data class TimeoutError(val message: String) : NetworkError()
    data class UnknownError(val message: String) : NetworkError()

    companion object {
        fun fromThrowable(throwable: Throwable): NetworkError {
            return when (throwable) {
                is java.net.SocketTimeoutException -> TimeoutError("Connection timeout")
                is java.net.ConnectException -> ConnectionError("No internet connection")
                is java.net.UnknownHostException -> ConnectionError("No internet connection")
                else -> UnknownError(throwable.message ?: "Unknown error")
            }
        }
    }
}