package org.duhen.stratus.hooks;

import android.content.Context;

import org.duhen.stratus.HookEntry;
import org.duhen.stratus.HookPrefs;

import java.io.File;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class PackageInstallerHook {

    private static final String ACTIVITY_CLASS =
            "com.android.packageinstaller.FlymePackageInstallerActivity";
    private static final int STATE_SCANNING = 2;

    private PackageInstallerHook() {}

    public static void install(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log("Stratus: loaded into PackageInstaller (" + lpparam.packageName + ")");
        hookStartSafeCheckService(lpparam);
        hookUpdateViewForNewState(lpparam);
    }

    private static boolean isSkipChecksEnabled(Context context) {
        return HookPrefs.getBoolean(context, HookEntry.PREF_SKIP_INSTALLER_CHECKS, false);
    }

    private static void hookStartSafeCheckService(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> activityClass = XposedHelpers.findClass(ACTIVITY_CLASS, lpparam.classLoader);

            XposedHelpers.findAndHookMethod(
                    activityClass,
                    "startSafeCheckService",
                    File.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            Context context = (Context) param.thisObject;
                            boolean enabled = isSkipChecksEnabled(context);
                            XposedBridge.log("Stratus: startSafeCheckService() CALLED"
                                    + ", enabled=" + enabled
                                    + ", prefsReady=" + (org.duhen.stratus.StratPrefs.getPrefs() != null));

                            if (!enabled) return;

                            Object instance = param.thisObject;
                            XposedHelpers.setBooleanField(instance, "mIsVirusCheckResultSafe", true);
                            XposedHelpers.setBooleanField(instance, "mIsVirusCheckFinish", true);

                            try {
                                Method onScanFinish = instance.getClass()
                                        .getDeclaredMethod("onScanFinish");
                                onScanFinish.setAccessible(true);
                                onScanFinish.invoke(instance);
                                XposedBridge.log("Stratus: onScanFinish() called successfully");
                            } catch (Throwable e) {
                                XposedBridge.log("Stratus: onScanFinish() call failed: " + e);
                            }

                            param.setResult(null);
                        }
                    }
            );

            XposedBridge.log("Stratus: startSafeCheckService hook installed");
        } catch (Throwable t) {
            XposedBridge.log("Stratus: startSafeCheckService hook failed: " + t);
        }
    }

    private static void hookUpdateViewForNewState(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> activityClass = XposedHelpers.findClass(ACTIVITY_CLASS, lpparam.classLoader);

            XposedHelpers.findAndHookMethod(
                    activityClass,
                    "updateViewForNewState",
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            int state = (int) param.args[0];
                            Context context = (Context) param.thisObject;
                            boolean enabled = isSkipChecksEnabled(context);
                            XposedBridge.log("Stratus: updateViewForNewState"
                                    + " state=" + state + ", enabled=" + enabled);

                            if (!enabled) return;
                            if (state == STATE_SCANNING) {
                                XposedBridge.log("Stratus: updateViewForNewState(SCANNING) suppressed");
                                param.setResult(null);
                            }
                        }
                    }
            );

            XposedBridge.log("Stratus: updateViewForNewState hook installed");
        } catch (Throwable t) {
            XposedBridge.log("Stratus: updateViewForNewState hook failed: " + t);
        }
    }
}
