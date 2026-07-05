package org.duhen.stratus.hooks;

import android.content.Context;

import org.duhen.stratus.HookPrefs;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class SuggestionWidgetHook {

    private static final String SUGGESTION_WIDGET_PKG = "com.meizu.suggestion";
    private static final String PREF_FIX_SUGGESTION_WIDGET = "pref_fix_suggestion_widget";

    private SuggestionWidgetHook() {}

    public static void install(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedBridge.log("Stratus: hooking suggestion widget in " + SUGGESTION_WIDGET_PKG);

            XposedHelpers.findAndHookMethod(
                    "com.meizu.suggestion.widget.apps.manager.a",
                    lpparam.classLoader,
                    "d",
                    Context.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (!HookPrefs.getBoolean(PREF_FIX_SUGGESTION_WIDGET, false)) return;
                            XposedBridge.log("Stratus: a.d(Context) hooked, forcing true");
                            param.setResult(true);
                        }
                    }
            );

            XposedBridge.log("Stratus: suggestion widget hook installed");
        } catch (Throwable t) {
            XposedBridge.log("Stratus: suggestion widget hook failed: " + t);
        }
    }
}
