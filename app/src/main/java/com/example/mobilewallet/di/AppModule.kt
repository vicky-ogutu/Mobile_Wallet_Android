package com.example.mobilewallet.di



import android.content.Context
import androidx.room.Room
import com.example.mobilewallet.data.local.Dao.LocalTransactionDao
import com.example.mobilewallet.data.local.database.WalletDatabase
import com.example.mobilewallet.data.local.datastore.PreferencesManager
import com.example.mobilewallet.data.local.repository.WalletRepository
import com.example.mobilewallet.remote.api.WalletApi
import com.example.mobilewallet.remote.api.service.RetrofitService
import com.example.mobilewallet.work.WorkManagerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideWalletDatabase(@ApplicationContext context: Context): WalletDatabase {
        return WalletDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideLocalTransactionDao(database: WalletDatabase) = database.localTransactionDao()

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideRetrofitService(): RetrofitService {
        return RetrofitService()
    }

    @Provides
    @Singleton
    fun provideWalletApi(retrofitService: RetrofitService) = retrofitService.walletApi

    @Provides
    @Singleton
    fun provideWalletRepository(
        api: WalletApi,
        localTransactionDao: LocalTransactionDao,
        preferencesManager: PreferencesManager
    ): WalletRepository {
        return WalletRepository(api, localTransactionDao, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideWorkManagerHelper(@ApplicationContext context: Context): WorkManagerHelper {
        return WorkManagerHelper(context)
    }
}