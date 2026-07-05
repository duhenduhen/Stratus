package org.duhen.stratus;

import android.os.Bundle;

public class SettingsFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferences(R.xml.preferences, rootKey);
    }
}
