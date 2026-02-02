package com.example.mobilewallet.work


import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobilewallet.data.local.entity.SyncStatus
import com.example.mobilewallet.data.local.repository.WalletRepository
import com.example.mobilewallet.models.SendMoneyRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WalletRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Get queued transactions
            val queuedTransactions = repository.getQueuedTransactions()

            for (transaction in queuedTransactions) {
                // Update status to SYNCING
                transaction.syncStatus = SyncStatus.SYNCING
                repository.updateTransaction(transaction)

                try {
                    // Send money to server
                    // Note: We need customerId here, we should store it in LocalTransaction
                    // For now, we'll get it from preferences
                    repository.customerId.collect { customerId ->
                        customerId?.let {
                            val request = SendMoneyRequest(
                                customerId = it,
                                accountFrom = transaction.accountFrom,
                                accountTo = transaction.accountTo,
                                amount = transaction.amount
                            )

                            val result = repository.sendMoneyRemote(request)

                            if (result.isSuccess) {
                                transaction.syncStatus = SyncStatus.SYNCED
                                transaction.lastError = null
                            } else {
                                transaction.syncStatus = SyncStatus.FAILED
                                transaction.lastError = result.exceptionOrNull()?.message
                                transaction.attemptCount += 1
                            }

                            repository.updateTransaction(transaction)
                        }
                    }

                    // Small delay between transactions
                    delay(1000)

                } catch (e: Exception) {
                    transaction.syncStatus = SyncStatus.FAILED
                    transaction.lastError = e.message
                    transaction.attemptCount += 1
                    repository.updateTransaction(transaction)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}