package org.duhen.stratus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;

public class SystemSettingsFragment extends BasePreferenceFragment {

    private Preference backgroundPreference;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                Uri uri = result.getData().getData();
                if (uri == null) return;
                try {
                    SettingsBackgroundStore.save(requireContext(), uri);
                    updateSummary();
                    Toast.makeText(requireContext(), R.string.toast_settings_bg_saved,
                            Toast.LENGTH_SHORT).show();
                } catch (Throwable t) {
                    Toast.makeText(requireContext(),
                            getString(R.string.toast_settings_bg_save_failed, t.getMessage()),
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferences(R.xml.preferences_system_settings, rootKey);
        backgroundPreference = findPreference("pref_settings_upgrade_background");
        if (backgroundPreference != null) {
            backgroundPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
                return true;
            });
        }
        Preference clearPreference = findPreference("pref_clear_settings_upgrade_background");
        if (clearPreference != null) {
            clearPreference.setOnPreferenceClickListener(preference -> {
                SettingsBackgroundStore.delete(requireContext());
                updateSummary();
                Toast.makeText(requireContext(), R.string.toast_settings_bg_cleared,
                        Toast.LENGTH_SHORT).show();
                return true;
            });
        }
        updateSummary();
    }

    private void updateSummary() {
        if (backgroundPreference == null) return;
        backgroundPreference.setSummary(SettingsBackgroundStore.exists(requireContext())
                ? R.string.pref_settings_upgrade_background_summary_set
                : R.string.pref_settings_upgrade_background_summary);
    }
}
