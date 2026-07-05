package org.duhen.stratus;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    protected void applyForwardAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            return;
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected void applyBackAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                    OVERRIDE_TRANSITION_OPEN,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
            return;
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
