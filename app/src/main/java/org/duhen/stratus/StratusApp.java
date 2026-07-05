package org.duhen.stratus;

import android.app.Application;
import android.content.Context;

import com.google.android.material.color.DynamicColors;

public class StratusApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.setLocale(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        LocaleHelper.setLocale(this);
    }
}
