package org.duhen.stratus;

import android.content.Context;
import android.util.Log;

import org.duhen.stratus.hooks.HideVpnIconHook;
import org.duhen.stratus.hooks.HideBatteryCriticalColorHook;
import org.duhen.stratus.hooks.HideLockscreenStatusBar;
import org.duhen.stratus.hooks.KeyguardIconsHook;
import org.duhen.stratus.hooks.LockscreenCarrierHook;
import org.duhen.stratus.hooks.PackageInstallerHook;
import org.duhen.stratus.hooks.SettingsResourceHook;
import org.duhen.stratus.hooks.SuggestionWidgetHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookInitPackageResources {

    public static final String PACKAGE_INSTALLER_PKG = "com.android.packageinstaller";
    public static final String SETTINGS_PKG = "com.android.settings";
    public static final String PREF_SKIP_INSTALLER_CHECKS = "pref_skip_installer_checks";

    private static final String SYSTEMUI_PKG = "com.android.systemui";
    private static final String SUGGESTION_WIDGET_PKG = "com.meizu.suggestion";

    private static volatile boolean initialized = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        switch (lpparam.packageName) {
            case SYSTEMUI_PKG -> {
                if (!initialized) {
                    initialized = true;
                    XposedBridge.log("Stratus: loaded into SystemUI");
                    try { Log.i("Stratus", "loaded into SystemUI"); } catch (Throwable ignored) {}
                }
                initializeStratPrefs(lpparam, "com.android.systemui.SystemUIApplication");
                KeyguardIconsHook.install(lpparam);
                LockscreenCarrierHook.install(lpparam);
                try {
                    new HideLockscreenStatusBar().handleLoadPackage(lpparam);
                } catch (Throwable t) {
                    XposedBridge.log("Stratus: HideLockscreenStatusBar failed: " + t);
                }
                HideVpnIconHook.install(lpparam);
                HideBatteryCriticalColorHook.install(lpparam);
            }
            case SUGGESTION_WIDGET_PKG -> {
                initializeStratPrefs(lpparam, "android.app.Application");
                SuggestionWidgetHook.install(lpparam);
            }
            case PACKAGE_INSTALLER_PKG -> {
                initializeStratPrefs(lpparam, "android.app.Application");
                PackageInstallerHook.install(lpparam);
            }
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        if (SETTINGS_PKG.equals(resparam.packageName)) {
            SettingsResourceHook.install(resparam);
        }
    }

    private void initializeStratPrefs(
            XC_LoadPackage.LoadPackageParam lpparam,
            String applicationClassName
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                    applicationClassName,
                    lpparam.classLoader,
                    "onCreate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            Context context = (param.thisObject instanceof Context c) ? c : null;
                            if (context == null) return;
                            try {
                                StratPrefs.init(context.getApplicationContext());
                                StratPrefs.registerPreferenceChangeListener();
                                try {
                                    Log.i("Stratus", "StratPrefs initialized in "
                                            + lpparam.packageName + " via " + applicationClassName);
                                } catch (Throwable ignored) {}
                            } catch (Throwable t) {
                                XposedBridge.log("Stratus: Failed to initialize StratPrefs: " + t);
                                try {
                                    Log.e("Stratus", "Failed to initialize StratPrefs in "
                                            + lpparam.packageName, t);
                                } catch (Throwable ignored) {}
                            }
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("Stratus: Failed to hook for StratPrefs: " + t);
            try {
                Log.e("Stratus", "Failed to hook for StratPrefs in " + lpparam.packageName
                        + " using " + applicationClassName, t);
            } catch (Throwable ignored) {}
        }
    }
}
