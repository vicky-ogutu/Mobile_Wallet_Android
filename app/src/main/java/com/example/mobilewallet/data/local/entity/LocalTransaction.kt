package com.example.mobilewallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "local_transactions")
data class LocalTransaction(
    @PrimaryKey
    val clientTransactionId: String = UUID.randomUUID().toString(),
    val accountFrom: String = "",
    val accountTo: String = "",
    val amount: Double = 0.0,
    val createdAt: Date = Date(),
    var syncStatus: SyncStatus = SyncStatus.QUEUED,
    var lastError: String? = null,
    var attemptCount: Int = 0
)

enum class SyncStatus {
    QUEUED, SYNCING, SYNCED, FAILED
}
