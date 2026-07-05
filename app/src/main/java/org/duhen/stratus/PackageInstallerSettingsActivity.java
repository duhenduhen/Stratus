package org.duhen.stratus;

import androidx.fragment.app.Fragment;

public class PackageInstallerSettingsActivity extends BaseSettingsActivity {

    @Override
    protected Fragment createFragment() {
        return new PackageInstallerSettingsFragment();
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.pref_category_package_installer);
    }
}
