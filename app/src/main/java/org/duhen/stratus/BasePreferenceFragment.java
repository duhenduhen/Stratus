package org.duhen.stratus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onAttach(Context context) {
        super.onAttach(LocaleHelper.setLocale(context));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView list = getListView();
        list.setClipToPadding(false);
        list.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        list.setPadding(dpToPx(8), 0, dpToPx(8), list.getPaddingBottom());
        ViewCompat.setOnApplyWindowInsetsListener(list, (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars()
                            | WindowInsetsCompat.Type.systemGestures()
            );
            v.setPadding(dpToPx(8), 0, dpToPx(8),
                    systemBars.bottom + dpToPx(24));
            return insets;
        });
    }

    protected final void setPreferences(int preferencesResId, String rootKey) {
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        getPreferenceManager().setSharedPreferencesName(
                requireContext().getPackageName() + "_preferences");
        setPreferencesFromResource(preferencesResId, rootKey);
        PreferenceUtils.fixPermissions(requireContext());
        PrefsUtil.ensurePrefsAccessible(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        if (preferences != null) preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        if (preferences != null) preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        performToggleHapticFeedback();
        PreferenceUtils.fixPermissions(requireContext());
        PrefsUtil.ensurePrefsAccessible(requireContext());
    }

    private void performToggleHapticFeedback() {
        Vibrator vibrator;
        VibratorManager vibratorManager = requireContext().getSystemService(VibratorManager.class);
        if (vibratorManager != null) {
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = requireContext().getSystemService(Vibrator.class);
        }
        if (vibrator == null || !vibrator.hasVibrator()) return;
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}
