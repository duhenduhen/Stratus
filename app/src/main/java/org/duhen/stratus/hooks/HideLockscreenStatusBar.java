package org.duhen.stratus.hooks;

import android.view.View;

import org.duhen.stratus.HookPrefs;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HideLockscreenStatusBar implements IXposedHookLoadPackage {

    private static final String SYSUI = "com.android.systemui";

    private static final String PREF_HIDE_LOCKSCREEN_STATUSBAR = "pref_hide_lockscreen_statusbar";

    private static boolean isEnabled() {
        return HookPrefs.getBoolean(PREF_HIDE_LOCKSCREEN_STATUSBAR, false);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(SYSUI)) return;

        hookKeyguardStatusBarViewController(lpparam.classLoader);
        hookKeyguardStatusBarView(lpparam.classLoader);

        hookFlymeBouncerStatusBarViewController(lpparam.classLoader);
        hookFlymeBouncerStatusBarView(lpparam.classLoader);
    }

    private void hookKeyguardStatusBarViewController(ClassLoader cl) {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.android.systemui.statusbar.phone.KeyguardStatusBarViewController",
                    cl,
                    "updateViewState",
                    float.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (!isEnabled()) return;
                            param.args[1] = View.GONE;
                        }
                    });

            XposedBridge.log("[HideStatusBar] KeyguardStatusBarViewController.updateViewState(FI) hooked OK");
        } catch (Throwable t) {
            XposedBridge.log("[HideStatusBar] KeyguardStatusBarViewController.updateViewState hook failed: " + t);
        }
    }

    private void hookKeyguardStatusBarView(ClassLoader cl) {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.android.systemui.statusbar.phone.KeyguardStatusBarView",
                    cl,
                    "onFinishInflate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            if (!isEnabled()) return;
                            hideAllChildren(param.thisObject,
                                    "com.android.systemui.statusbar.phone.KeyguardStatusBarView");
                        }
                    });

            XposedBridge.log("[HideStatusBar] KeyguardStatusBarView.onFinishInflate hooked OK");
        } catch (Throwable t) {
            XposedBridge.log("[HideStatusBar] KeyguardStatusBarView.onFinishInflate hook failed: " + t);
        }
    }

    private void hookFlymeBouncerStatusBarViewController(ClassLoader cl) {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.flyme.statusbar.bouncer.KeyguardBouncerStatusBarViewController",
                    cl,
                    "updateViewState",
                    float.class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (!isEnabled()) return;
                            param.args[1] = View.GONE;
                        }
                    });

            XposedBridge.log("[HideStatusBar] FlymeBouncerViewController.updateViewState(FI) hooked OK");
        } catch (Throwable t) {
            XposedBridge.log("[HideStatusBar] FlymeBouncerViewController.updateViewState hook failed: " + t);
        }
    }

    private void hookFlymeBouncerStatusBarView(ClassLoader cl) {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.flyme.statusbar.bouncer.KeyguardBouncerStatusBarView",
                    cl,
                    "onFinishInflate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            if (!isEnabled()) return;
                            hideAllChildren(param.thisObject,
                                    "com.flyme.statusbar.bouncer.KeyguardBouncerStatusBarView");
                        }
                    });

            XposedBridge.log("[HideStatusBar] FlymeBouncerStatusBarView.onFinishInflate hooked OK");
        } catch (Throwable t) {
            XposedBridge.log("[HideStatusBar] FlymeBouncerStatusBarView.onFinishInflate hook failed: " + t);
        }
    }

    private void hideAllChildren(Object viewObj, String className) {
        String[] viewFields = {
                "mSystemIconsContainer", // R$id.system_icons_container
                "mSystemIcons", // R$id.system_icons
                "mStatusIconArea", // R$id.status_icon_area
                "mStatusIconContainer", // R$id.statusIcons
                "mCarrierLabel", // R$id.keyguard_carrier_text
                "mMultiUserAvatar", // R$id.multi_user_avatar
                "mUserSwitcherContainer", // R$id.user_switcher_container
                "mCutoutSpace", // R$id.cutout_space_view
                "mBatteryMeterView", // R$id.battery
                "mBatteryTextView", // R$id.battery_percent
                "mClockWrapper", // R$id.keyguard_clock_wrapper
                "mClock", // R$id.keyguard_clock
                "mDate", // R$id.date
        };

        for (String field : viewFields) {
            try {
                Object v = XposedHelpers.getObjectField(viewObj, field);
                if (v instanceof View) {
                    ((View) v).setVisibility(View.GONE);
                }
            } catch (Throwable t) {
                XposedBridge.log("[HideStatusBar] " + className + "." + field + ": " + t.getMessage());
            }
        }

        try {
            ((View) viewObj).setVisibility(View.GONE);
        } catch (Throwable t) {
            XposedBridge.log("[HideStatusBar] setVisibility on root failed: " + t.getMessage());
        }
    }
}