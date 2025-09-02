package top.itning.yunshuclassschedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

object LocaleHelper {
    private const val KEY_LANG = "app_language"

    fun getSavedLang(ctx: Context): String {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        return sp.getString(KEY_LANG, "system") ?: "system"
    }

    fun applyAppLocale(base: Context): Context {
        val lang = getSavedLang(base)
        val locale = when (lang) {
            "zh" -> Locale.CHINESE
            "en" -> Locale.ENGLISH
            else -> getSystemLocale() // "system"
        }
        return updateContextLocale(base, locale)
    }

    fun saveLang(ctx: Context, value: String) {
        PreferenceManager.getDefaultSharedPreferences(ctx)
            .edit().putString(KEY_LANG, value).apply()
    }

    private fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= 24)
            Resources.getSystem().configuration.locales[0]
        else
            Resources.getSystem().configuration.locale
    }

    private fun updateContextLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= 24) {
            config.setLocale(locale)
            config.setLocales(android.os.LocaleList(locale))
            return context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            res.updateConfiguration(config, res.displayMetrics)
            return context
        }
    }
}
