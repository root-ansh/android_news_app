package io.github.curioustools.curiousnews.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import javax.inject.Inject

class SharedPrefs  @Inject constructor(context: Context) {
    val userSettings = UserSettings(context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE))

    fun getCurrentThemeInfo() = userSettings.themeType to userSettings.useDynamicColor
    fun isChangedKeyThemeKey(key: String?) = key.equals("themeType",true)


    enum class ThemeMode {
        LIGHT, DARK, SYSTEM;


        fun icon() = when(this){
            LIGHT -> Icons.Default.LightMode
            DARK -> Icons.Default.DarkMode
            SYSTEM -> Icons.Default.AutoMode
        }

    }

    class UserSettings internal constructor(private val pref: SharedPreferences){
        var useDynamicColor: Boolean
            get() = pref.getBoolean("useDynamicColor", false)
            set(value) { pref.edit().putBoolean("useDynamicColor", value).apply() }

        var themeType: ThemeMode
            get() {
                val value = pref.getString("themeType", ThemeMode.LIGHT.name)
                return ThemeMode.valueOf(value ?: ThemeMode.LIGHT.name)
            }
            set(value) { pref.edit().putString("themeType", value.name).apply() }

        fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener){
            pref.registerOnSharedPreferenceChangeListener(listener)
        }
        fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener){
            pref.unregisterOnSharedPreferenceChangeListener(listener)
        }

    }

}