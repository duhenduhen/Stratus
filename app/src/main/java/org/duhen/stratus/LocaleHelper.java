package org.duhen.stratus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import java.util.Locale;

public final class LocaleHelper {

    private static final String PREF_LANGUAGE = "pref_app_language";
    private static final String DEFAULT_LANGUAGE = "";

    private LocaleHelper() {}

    public static Context setLocale(Context context) {
        return updateResources(context, getLanguage(context));
    }

    public static void setNewLocale(Context context, String language) {
        persistLanguage(context, language);
        updateResources(context, language);
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = getPrefsOrNull(context);
        return prefs != null ? prefs.getString(PREF_LANGUAGE, DEFAULT_LANGUAGE) : DEFAULT_LANGUAGE;
    }

    public static String getLanguageDisplayName(String code) {
        return switch (code) {
            case "en" -> "English";
            case "tr" -> "Türkçe";
            default  -> "System Default";
        };
    }

    public static String[] getLanguageEntryValues() {
        return new String[]{"", "en", "tr"};
    }

    public static String[] getLanguageEntries() {
        return new String[]{"language_system_default", "language_english", "language_turkish"};
    }

    public static String getEffectiveLanguage(Context context) {
        SharedPreferences prefs = getPrefsOrNull(context);
        String appLanguage = prefs != null
                ? prefs.getString(PREF_LANGUAGE, DEFAULT_LANGUAGE)
                : DEFAULT_LANGUAGE;
        if (appLanguage.isEmpty()) {
            return Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        }
        return appLanguage;
    }

    public static boolean isTurkish(Context context) {
        return "tr".equals(getEffectiveLanguage(context));
    }

    private static void persistLanguage(Context context, String language) {
        SharedPreferences prefs = getPrefsOrNull(context);
        if (prefs != null) prefs.edit().putString(PREF_LANGUAGE, language).apply();
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = switch (language) {
            case "en" -> new Locale("en");
            case "tr" -> new Locale("tr");
            default  -> Resources.getSystem().getConfiguration().getLocales().get(0);
        };

        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        return context.createConfigurationContext(config);
    }

    private static SharedPreferences getPrefsOrNull(Context context) {
        try {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } catch (Throwable t) {
            return null;
        }
    }
}
