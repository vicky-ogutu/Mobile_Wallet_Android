package com.example.mobilewallet.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mobilewallet.local.entity.LocalTransaction
import com.example.mobilewallet.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTransactionDao {
    @Insert
    suspend fun insert(transaction: LocalTransaction)

    @Update
    suspend fun update(transaction: LocalTransaction)

    @Query("SELECT * FROM local_transactions ORDER BY createdAt DESC")
    fun getAll(): Flow<List<LocalTransaction>>

    @Query("SELECT * FROM local_transactions WHERE syncStatus = :status")
    suspend fun getByStatus(status: SyncStatus): List<LocalTransaction>

    @Query("SELECT * FROM local_transactions WHERE clientTransactionId = :id")
    suspend fun getById(id: String): LocalTransaction?

    @Query("DELETE FROM local_transactions WHERE syncStatus = :status")
    suspend fun deleteByStatus(status: SyncStatus)

    @Query("DELETE FROM local_transactions WHERE clientTransactionId = :id")
    suspend fun deleteById(id: String)
}