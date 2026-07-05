package org.duhen.stratus;

import android.os.Bundle;

public class StatusBarSettingsFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferences(R.xml.preferences_statusbar, rootKey);
    }
}
