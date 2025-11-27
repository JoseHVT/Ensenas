package com.example.chat_bot.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore para preferencias de usuario
 */
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    
    companion object {
        // Keys para preferencias
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val DAILY_REMINDER = booleanPreferencesKey("daily_reminder")
        private val REMINDER_TIME = stringPreferencesKey("reminder_time")
        private val VIDEO_SPEED = floatPreferencesKey("video_speed")
        private val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        private val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
        private val LANGUAGE = stringPreferencesKey("language")
    }
    
    /**
     * Notificaciones habilitadas
     */
    val notificationsEnabled: Flow<Boolean> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    /**
     * Recordatorio diario
     */
    val dailyReminderEnabled: Flow<Boolean> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[DAILY_REMINDER] ?: true
        }
    
    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[DAILY_REMINDER] = enabled
        }
    }
    
    /**
     * Hora del recordatorio (formato HH:mm)
     */
    val reminderTime: Flow<String> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[REMINDER_TIME] ?: "20:00"
        }
    
    suspend fun setReminderTime(time: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[REMINDER_TIME] = time
        }
    }
    
    /**
     * Velocidad de reproducción de videos (0.5, 0.75, 1.0, 1.25, 1.5, 2.0)
     */
    val videoSpeed: Flow<Float> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[VIDEO_SPEED] ?: 1.0f
        }
    
    suspend fun setVideoSpeed(speed: Float) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[VIDEO_SPEED] = speed
        }
    }
    
    /**
     * Modo oscuro habilitado
     */
    val darkModeEnabled: Flow<Boolean> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[DARK_MODE_ENABLED] ?: false
        }
    
    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED] = enabled
        }
    }
    
    /**
     * Analytics habilitado (privacidad)
     */
    val analyticsEnabled: Flow<Boolean> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[ANALYTICS_ENABLED] ?: true
        }
    
    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[ANALYTICS_ENABLED] = enabled
        }
    }
    
    /**
     * Idioma de la app
     */
    val language: Flow<String> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[LANGUAGE] ?: "es"
        }
    
    suspend fun setLanguage(lang: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[LANGUAGE] = lang
        }
    }
    
    /**
     * Limpia todas las preferencias
     */
    suspend fun clearAll() {
        context.userPreferencesDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
