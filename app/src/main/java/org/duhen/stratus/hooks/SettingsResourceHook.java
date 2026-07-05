package org.duhen.stratus.hooks;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.res.XResources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.duhen.stratus.SettingsBackgroundStore;

import java.io.InputStream;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

public final class SettingsResourceHook {

    private static final String SETTINGS_PKG = "com.android.settings";

    private SettingsResourceHook() {}

    public static void install(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        try {
            resparam.res.setReplacement(
                    SETTINGS_PKG,
                    "drawable",
                    "bg_system_upgrade",
                    new XResources.DrawableLoader() {
                        @Override
                        public Drawable newDrawable(XResources res, int id) {
                            Context context = AndroidAppHelper.currentApplication();
                            if (context == null) {
                                XposedBridge.log("Stratus: Settings bg load failed: no context");
                                return null;
                            }
                            try (InputStream in = context.getContentResolver()
                                    .openInputStream(SettingsBackgroundStore.CONTENT_URI)) {
                                if (in == null) return null;
                                return new BitmapDrawable(res, BitmapFactory.decodeStream(in));
                            } catch (Throwable t) {
                                XposedBridge.log("Stratus: Settings bg load failed: " + t);
                                return null;
                            }
                        }
                    });
            XposedBridge.log("Stratus: Settings bg_system_upgrade replacement installed");
        } catch (Throwable t) {
            XposedBridge.log("Stratus: Settings resource hook failed: " + t);
        }
    }
}
