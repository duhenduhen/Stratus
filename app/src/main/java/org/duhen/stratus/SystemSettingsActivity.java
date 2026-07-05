package org.duhen.stratus;

import androidx.fragment.app.Fragment;

public class SystemSettingsActivity extends BaseSettingsActivity {

    @Override
    protected Fragment createFragment() {
        return new SystemSettingsFragment();
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.pref_category_system_settings);
    }
}
