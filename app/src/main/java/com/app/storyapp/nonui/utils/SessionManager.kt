package com.app.storyapp.nonui.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "story_app_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true) // Simpan status login
        editor.apply()
        Log.d("SessionManager", "Session saved: Token = $token, Logged In = true")
    }


    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        Log.d("SessionManager", "isLoggedIn: $isLoggedIn")
        return isLoggedIn
    }


    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
