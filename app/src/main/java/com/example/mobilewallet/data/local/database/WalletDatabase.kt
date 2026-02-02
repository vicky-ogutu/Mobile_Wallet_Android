package com.example.mobilewallet.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.mobilewallet.data.local.Dao.LocalTransactionDao
import com.example.mobilewallet.data.local.coverter.DateConverter
import com.example.mobilewallet.data.local.entity.LocalTransaction


@Database(
    entities = [LocalTransaction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class WalletDatabase : RoomDatabase() {
    abstract fun localTransactionDao(): LocalTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: WalletDatabase? = null

        fun getInstance(context: Context): WalletDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WalletDatabase::class.java,
                    "wallet_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}