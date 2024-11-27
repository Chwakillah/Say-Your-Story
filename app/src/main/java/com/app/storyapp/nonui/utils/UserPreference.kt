package com.app.storyapp.nonui.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getLoginState(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]?.isNotEmpty() ?: false
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveLoginSession(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearLoginSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}