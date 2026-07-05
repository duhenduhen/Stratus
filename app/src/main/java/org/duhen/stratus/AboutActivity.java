package org.duhen.stratus;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_about);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                applyForwardAnimation();
            }
        });

        AppBarLayout appBar = findViewById(R.id.appbar);
        ViewCompat.setOnApplyWindowInsetsListener(appBar, (view, insets) -> {
            view.setPadding(
                    view.getPaddingLeft(),
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            );
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.about_title);
        }

        TextView version = findViewById(R.id.about_version);
        version.setText(getString(R.string.about_version, getVersionName()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        applyForwardAnimation();
        return true;
    }

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Throwable ignored) {
            return "";
        }
    }
}
