package org.duhen.stratus.hooks;

import android.view.View;

import org.duhen.stratus.HookPrefs;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class KeyguardIconsHook {

    private static final String TARGET_CLASS =
            "com.flyme.systemui.affordance.MZKeyguardBottomAreaView";
    private static final String PREF_HIDE_KEYGUARD_ICONS = "pref_hide_keyguard_icons";

    private KeyguardIconsHook() {}

    public static void install(XC_LoadPackage.LoadPackageParam lpparam) {
        hookVisibilityMethod(lpparam, "updateLeftClickVisibility",  "mLeftClickAffordanceView");
        hookVisibilityMethod(lpparam, "updateRightVisibility",      "mRightClickAffordanceView");
    }

    private static void hookVisibilityMethod(
            XC_LoadPackage.LoadPackageParam lpparam,
            String methodName,
            String fieldName
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                    TARGET_CLASS,
                    lpparam.classLoader,
                    methodName,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (!HookPrefs.getBoolean(PREF_HIDE_KEYGUARD_ICONS, false)) return;
                            XposedBridge.log("Stratus: " + methodName + " intercepted");
                            setHidden(param.thisObject, fieldName);
                            param.setResult(null);
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("Stratus: " + methodName + " hook failed: " + t);
        }
    }

    private static void setHidden(Object instance, String fieldName) {
        try {
            Object obj = XposedHelpers.getObjectField(instance, fieldName);
            if (obj instanceof View view) {
                view.setVisibility(View.GONE);
            }
        } catch (Throwable t) {
            XposedBridge.log("Stratus: setHidden(" + fieldName + ") failed: " + t);
        }
    }
}
