package org.duhen.stratus;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

public abstract class BaseSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_settings);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                applyForwardAnimation();
            }
        });

        AppBarLayout appBar = findViewById(R.id.appbar);
        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            v.setPadding(v.getPaddingLeft(),
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                    v.getPaddingRight(), 0);
            return insets;
        });
        appBar.setLiftOnScroll(true);
        appBar.setStatusBarForegroundColor(android.graphics.Color.TRANSPARENT);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = getScreenTitle();
            if (title != null) getSupportActionBar().setTitle(title);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, createFragment())
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        applyForwardAnimation();
        return true;
    }

    protected abstract Fragment createFragment();

    protected String getScreenTitle() {
        return null;
    }
}
