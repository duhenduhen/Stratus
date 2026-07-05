package org.duhen.stratus;

import androidx.fragment.app.Fragment;

public class SettingsActivity extends BaseSettingsActivity {

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.pref_category_lockscreen);
    }
}
