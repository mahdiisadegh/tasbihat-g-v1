package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isMusicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()

    var isDhikrSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_DHIKR_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_DHIKR_SOUND_ENABLED, value).apply()

    companion object {
        private const val PREFS_NAME = "tasbihat_preferences"
        private const val KEY_MUSIC_ENABLED = "key_music_enabled"
        private const val KEY_DHIKR_SOUND_ENABLED = "key_dhikr_sound_enabled"
    }
}
