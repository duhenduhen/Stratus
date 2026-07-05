package org.duhen.stratus;

import android.os.Bundle;

public class PackageInstallerSettingsFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferences(R.xml.preferences_package_installer, rootKey);
    }
}
