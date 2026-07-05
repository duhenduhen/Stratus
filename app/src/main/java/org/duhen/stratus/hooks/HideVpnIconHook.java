package org.duhen.stratus.hooks;

import org.duhen.stratus.HookPrefs;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class HideVpnIconHook {

    private static final String TARGET_CLASS =
            "com.android.systemui.statusbar.phone.StatusBarSignalPolicy";
    private static final String PREF_HIDE_VPN_ICON = "pref_hide_vpn_icon";

    private HideVpnIconHook() {}

    public static void install(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(
                    TARGET_CLASS,
                    lpparam.classLoader,
                    "updateVpn",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (!HookPrefs.getBoolean(PREF_HIDE_VPN_ICON, false)) return;
                            XposedBridge.log("Stratus: updateVpn intercepted, hiding VPN icon");
                            param.setResult(null);
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("Stratus: updateVpn hook failed: " + t);
        }
    }
}
