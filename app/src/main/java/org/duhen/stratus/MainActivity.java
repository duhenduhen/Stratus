package org.duhen.stratus;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.topjohnwu.superuser.Shell;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        PrefsUtil.ensurePrefsAccessible(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        AppBarLayout appBar = findViewById(R.id.appbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(appBar, (view, insets) -> {
            view.setPadding(
                    view.getPaddingLeft(),
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            );
            return insets;
        });
        setupTitleTransition(appBar, collapsingToolbar);

        setupCategory(
                R.id.category_lockscreen,
                R.id.lockscreen_icon,
                R.id.lockscreen_title,
                R.drawable.ic_lockscreen,
                com.google.android.material.R.attr.colorPrimaryContainer,
                com.google.android.material.R.attr.colorOnPrimaryContainer,
                R.string.pref_category_lockscreen,
                SettingsActivity.class
        );
        setupCategory(
                R.id.category_widgets,
                R.id.widgets_icon,
                R.id.widgets_title,
                R.drawable.ic_widgets,
                com.google.android.material.R.attr.colorSecondaryContainer,
                com.google.android.material.R.attr.colorOnSecondaryContainer,
                R.string.pref_category_widgets,
                WidgetsSettingsActivity.class
        );
        setupCategory(
                R.id.category_statusbar,
                R.id.statusbar_icon,
                R.id.statusbar_title,
                R.drawable.ic_statusbar,
                com.google.android.material.R.attr.colorTertiaryContainer,
                com.google.android.material.R.attr.colorOnTertiaryContainer,
                R.string.pref_category_statusbar,
                StatusBarSettingsActivity.class
        );
        setupCategory(
                R.id.category_package_installer,
                R.id.installer_icon,
                R.id.installer_title,
                R.drawable.ic_installer,
                com.google.android.material.R.attr.colorTertiaryContainer,
                com.google.android.material.R.attr.colorOnTertiaryContainer,
                R.string.pref_category_package_installer,
                PackageInstallerSettingsActivity.class
        );
        setupCategory(
                R.id.category_system_settings,
                R.id.system_settings_icon,
                R.id.system_settings_title,
                R.drawable.ic_settings,
                com.google.android.material.R.attr.colorPrimaryContainer,
                com.google.android.material.R.attr.colorOnPrimaryContainer,
                R.string.pref_category_system_settings,
                SystemSettingsActivity.class
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restart_systemui) {
            restartSystemUI();
            return true;
        }
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            applyBackAnimation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupCategory(
            int cardId,
            int iconId,
            int titleId,
            int iconRes,
            int bgColorAttr,
            int iconColorAttr,
            int titleRes,
            Class<?> target
    ) {
        MaterialCardView card = findViewById(cardId);

        ImageView icon = findViewById(iconId);
        icon.setImageResource(iconRes);
        icon.getBackground().setTintList(
                ColorStateList.valueOf(resolveThemeColor(bgColorAttr)));
        icon.setImageTintList(
                ColorStateList.valueOf(resolveThemeColor(iconColorAttr)));

        ((TextView) findViewById(titleId)).setText(titleRes);

        card.setOnClickListener(v -> {
            startActivity(new Intent(this, target));
            applyBackAnimation();
        });
    }

    private int resolveThemeColor(int attr) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    private void setupTitleTransition(
            AppBarLayout appBar,
            CollapsingToolbarLayout collapsingToolbar
    ) {
        int baseColor = resolveThemeColor(com.google.android.material.R.attr.colorOnSurface)
                & 0x00FFFFFF;
        appBar.addOnOffsetChangedListener((bar, verticalOffset) -> {
            int total = bar.getTotalScrollRange();
            if (total == 0) return;
            float progress = Math.min(1f, Math.abs(verticalOffset) / (float) total);
            int expandedAlpha = Math.round(255 * (1f - progress));
            collapsingToolbar.setExpandedTitleColor((expandedAlpha << 24) | baseColor);
            collapsingToolbar.setCollapsedTitleTextColor((0xFF << 24) | baseColor);
        });
    }

    private void restartSystemUI() {
        try {
            Shell.Result result = Shell.cmd("killall com.android.systemui").exec();
            if (!result.isSuccess()) {
                throw new IllegalStateException(String.join("\n", result.getErr()));
            }

            PrefsUtil.ensurePrefsAccessible(this);
            scheduleRestartSelf();
            Toast.makeText(this, R.string.toast_systemui_restarting, Toast.LENGTH_SHORT).show();
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    getString(R.string.toast_systemui_restart_failed, e.getMessage()),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void scheduleRestartSelf() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (intent == null) intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }, 1200);
    }
}