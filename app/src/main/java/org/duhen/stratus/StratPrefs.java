package org.duhen.stratus;

import android.content.Context;
import android.content.SharedPreferences;

import com.crossbowffs.remotepreferences.RemotePreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;

public final class StratPrefs {

    private static final String TAG = "Stratus";
    private static final String AUTHORITY = "org.duhen.stratus";
    private static final String PREF_FILE  = "org.duhen.stratus_preferences";

    private static volatile RemotePreferences prefs = null;
    private static volatile boolean prefsInitialized = false;

    private static final List<OnPreferenceUpdateListener> listeners =
            Collections.synchronizedList(new ArrayList<>());

    private static final SharedPreferences.OnSharedPreferenceChangeListener sharedPrefsListener =
            (sharedPreferences, key) -> {
                XposedBridge.log(TAG + ": Preference changed: " + key);
                notifyPreferenceUpdate(key);
            };

    private StratPrefs() {}

    public static RemotePreferences getPrefs() {
        return prefs;
    }

    public static void init(Context context) {
        if (prefsInitialized) return;
        try {
            prefs = new RemotePreferences(context, AUTHORITY, PREF_FILE, true);
            prefsInitialized = true;
            XposedBridge.log(TAG + ": RemotePreferences initialized");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": Failed to init RemotePreferences: " + t);
        }
    }

    public static void registerPreferenceChangeListener() {
        try {
            if (prefs != null) {
                prefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener);
            }
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": Failed to register pref listener: " + t);
        }
    }

    public static void addOnPreferenceUpdateListener(OnPreferenceUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeOnPreferenceUpdateListener(OnPreferenceUpdateListener listener) {
        listeners.remove(listener);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        ensureInit();
        try {
            return prefs != null ? prefs.getBoolean(key, defValue) : defValue;
        } catch (Throwable t) {
            return defValue;
        }
    }

    public static String getString(String key, String defValue) {
        ensureInit();
        try {
            String result = prefs != null ? prefs.getString(key, defValue) : defValue;
            return result != null ? result : defValue;
        } catch (Throwable t) {
            return defValue;
        }
    }

    public static int getInt(String key, int defValue) {
        ensureInit();
        try {
            return prefs != null ? prefs.getInt(key, defValue) : defValue;
        } catch (Throwable t) {
            return defValue;
        }
    }

    public static Set<String> getStringSet(String key, Set<String> defValue) {
        ensureInit();
        try {
            Set<String> result = prefs != null ? prefs.getStringSet(key, defValue) : defValue;
            return result != null ? result : defValue;
        } catch (Throwable t) {
            return defValue;
        }
    }

    private static void ensureInit() {
        if (prefsInitialized) return;
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Context app = (Context) activityThread.getMethod("currentApplication").invoke(null);
            if (app != null) {
                init(app.getApplicationContext());
            }
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": ensureInit failed: " + t);
        }
    }

    private static void notifyPreferenceUpdate(String key) {
        synchronized (listeners) {
            for (OnPreferenceUpdateListener listener : listeners) {
                try {
                    listener.onPreferenceUpdated(key);
                } catch (Throwable t) {
                    XposedBridge.log(TAG + ": Error notifying listener: " + t);
                }
            }
        }
    }

    @FunctionalInterface
    public interface OnPreferenceUpdateListener {
        void onPreferenceUpdated(String key);
    }
}
