package com.example.mobilewallet.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mobilewallet.models.Customer
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wallet_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val gson = Gson()

    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val CUSTOMER = stringPreferencesKey("customer")
        val CUSTOMER_ID = stringPreferencesKey("customer_id")
        val CUSTOMER_NAME = stringPreferencesKey("customer_name")
        val CUSTOMER_EMAIL = stringPreferencesKey("customer_email")
        val CUSTOMER_ACCOUNT = stringPreferencesKey("customer_account")
        val CUSTOMER_PHONE = stringPreferencesKey("customer_phone") // Add this key
    }

    suspend fun saveLogin(customer: Customer, accountNo: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.CUSTOMER] = gson.toJson(customer)
            preferences[PreferencesKeys.CUSTOMER_ID] = customer.customerId
            preferences[PreferencesKeys.CUSTOMER_NAME] = customer.fullName // Assuming Customer has 'name' property
            preferences[PreferencesKeys.CUSTOMER_EMAIL] = customer.email
            preferences[PreferencesKeys.CUSTOMER_ACCOUNT] = accountNo
         
        }
    }

    suspend fun clearLogin() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.IS_LOGGED_IN)
            preferences.remove(PreferencesKeys.CUSTOMER)
            preferences.remove(PreferencesKeys.CUSTOMER_ID)
            preferences.remove(PreferencesKeys.CUSTOMER_NAME)
            preferences.remove(PreferencesKeys.CUSTOMER_EMAIL)
            preferences.remove(PreferencesKeys.CUSTOMER_ACCOUNT)
            preferences.remove(PreferencesKeys.CUSTOMER_PHONE)
        }
    }

    // Suspend function to get current customer
    suspend fun getCurrentCustomer(): Customer? {
        return customer.first()
    }

    // Suspend function to get current account
    suspend fun getCurrentAccount(): String? {
        return customerAccount.first()
    }

    // Suspend function to get customer ID
    suspend fun getCustomerId(): String? {
        return customerId.first()
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }

    val customer: Flow<Customer?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CUSTOMER]?.let {
                try {
                    gson.fromJson(it, Customer::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }

    val customerId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CUSTOMER_ID]
        }

    val customerName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CUSTOMER_NAME]
        }

    val customerEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CUSTOMER_EMAIL]
        }

    val customerAccount: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CUSTOMER_ACCOUNT]
        }
}