package org.duhen.stratus;

import androidx.fragment.app.Fragment;

public class StatusBarSettingsActivity extends BaseSettingsActivity {

    @Override
    protected Fragment createFragment() {
        return new StatusBarSettingsFragment();
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.pref_category_statusbar);
    }
}
