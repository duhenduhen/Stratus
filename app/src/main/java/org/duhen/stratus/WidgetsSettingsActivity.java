package org.duhen.stratus;

import androidx.fragment.app.Fragment;

public class WidgetsSettingsActivity extends BaseSettingsActivity {

    @Override
    protected Fragment createFragment() {
        return new WidgetsSettingsFragment();
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.pref_category_widgets);
    }
}
