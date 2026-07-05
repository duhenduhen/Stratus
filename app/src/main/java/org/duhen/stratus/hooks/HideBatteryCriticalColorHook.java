package org.duhen.stratus.hooks;

import org.duhen.stratus.HookPrefs;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class HideBatteryCriticalColorHook {

    private static final String TARGET_CLASS =
            "com.flyme.statusbar.battery.FlymeBatteryTextView";
    private static final String PREF_HIDE_BATTERY_CRITICAL_COLOR = "pref_hide_battery_critical_color";

    private HideBatteryCriticalColorHook() {
    }

    public static void install(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(
                    TARGET_CLASS,
                    lpparam.classLoader,
                    "setLowColorMode",
                    boolean.class,
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (!HookPrefs.getBoolean(PREF_HIDE_BATTERY_CRITICAL_COLOR, false))
                                return;
                            XposedBridge.log("Stratus: setLowColorMode intercepted, suppressing low battery color");
                            // Force isLow = false so the red color is never applied
                            param.args[0] = false;
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("Stratus: setLowColorMode hook failed: " + t);
        }
    }
}
