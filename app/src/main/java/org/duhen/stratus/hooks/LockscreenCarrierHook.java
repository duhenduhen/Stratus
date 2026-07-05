package org.duhen.stratus.hooks;

import android.view.View;
import android.widget.TextView;

import org.duhen.stratus.HookPrefs;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class LockscreenCarrierHook {

    private static final String PREF_HIDE_LOCKSCREEN_CARRIER = "pref_hide_lockscreen_carrier";

    private LockscreenCarrierHook() {}

    public static void install(XC_LoadPackage.LoadPackageParam lpparam) {
        hookPostToCallback(lpparam);
        hookCarrierLabel(lpparam, "com.android.systemui.statusbar.phone.KeyguardStatusBarView");
        hookCarrierLabel(lpparam, "com.flyme.statusbar.bouncer.KeyguardBouncerStatusBarView");
    }

    private static boolean isEnabled() {
        return HookPrefs.getBoolean(PREF_HIDE_LOCKSCREEN_CARRIER, false);
    }

    private static void hookPostToCallback(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> infoClass = XposedHelpers.findClass(
                    "com.android.keyguard.CarrierTextManager$CarrierTextCallbackInfo",
                    lpparam.classLoader);

            XposedHelpers.findAndHookMethod(
                    "com.android.keyguard.CarrierTextManager",
                    lpparam.classLoader,
                    "postToCallback",
                    infoClass,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (!isEnabled()) return;
                            Object info = param.args[0];
                            if (info == null) return;
                            try {
                                XposedHelpers.setObjectField(info, "carrierText", "");
                            } catch (Throwable t) {
                                XposedBridge.log("Stratus: carrierText clear failed: " + t);
                            }
                        }
                    }
            );

            XposedBridge.log("Stratus: CarrierTextManager.postToCallback hook installed");
        } catch (Throwable t) {
            XposedBridge.log("Stratus: CarrierTextManager.postToCallback hook failed: " + t);
        }
    }

    private static void hookCarrierLabel(
            XC_LoadPackage.LoadPackageParam lpparam,
            String className
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                    className,
                    lpparam.classLoader,
                    "onFinishInflate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            if (!isEnabled()) return;
                            hideCarrierLabel(param.thisObject, className);
                        }
                    }
            );

            XposedBridge.log("Stratus: " + className + ".onFinishInflate hook installed");
        } catch (Throwable t) {
            XposedBridge.log("Stratus: " + className + ".onFinishInflate hook failed: " + t);
        }
    }

    private static void hideCarrierLabel(Object instance, String source) {
        try {
            Object carrierLabel = XposedHelpers.getObjectField(instance, "mCarrierLabel");
            if (carrierLabel instanceof TextView textView) {
                textView.setText("");
                textView.setVisibility(View.GONE);
            }
        } catch (Throwable t) {
            XposedBridge.log("Stratus: hideCarrierLabel(" + source + ") failed: " + t);
        }
    }
}
