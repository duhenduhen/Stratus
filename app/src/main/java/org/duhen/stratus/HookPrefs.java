package org.duhen.stratus;

import android.content.Context;

import de.robv.android.xposed.XposedBridge;

public final class HookPrefs {

    private static final String MODULE_PACKAGE = "org.duhen.stratus";

    private HookPrefs() {}

    public static boolean getBoolean(String key, boolean defValue) {
        boolean value = StratPrefs.getBoolean(key, defValue);
        if (StratPrefs.getPrefs() != null) return value;

        Context context = currentApplication();
        return context != null ? getBoolean(context, key, defValue) : value;
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        boolean value = StratPrefs.getBoolean(key, defValue);
        if (StratPrefs.getPrefs() != null) return value;

        try {
            Context moduleContext = context.createPackageContext(
                    MODULE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY);
            return moduleContext
                    .getSharedPreferences(RemotePrefProvider.PREF_FILE, Context.MODE_PRIVATE)
                    .getBoolean(key, defValue);
        } catch (Throwable t) {
            XposedBridge.log("Stratus: CE prefs fallback failed for " + key + ": " + t);
        }

        try {
            Context moduleContext = context.createPackageContext(
                    MODULE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
                    .createDeviceProtectedStorageContext();
            return moduleContext
                    .getSharedPreferences(RemotePrefProvider.PREF_FILE, Context.MODE_PRIVATE)
                    .getBoolean(key, defValue);
        } catch (Throwable t) {
            XposedBridge.log("Stratus: DE prefs fallback failed for " + key + ": " + t);
            return defValue;
        }
    }

    private static Context currentApplication() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object app = activityThread.getMethod("currentApplication").invoke(null);
            return app instanceof Context ? (Context) app : null;
        } catch (Throwable t) {
            XposedBridge.log("Stratus: currentApplication fallback failed: " + t);
            return null;
        }
    }
}
