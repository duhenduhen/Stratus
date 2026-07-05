package org.duhen.stratus;

import android.os.Bundle;

public class WidgetsSettingsFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferences(R.xml.preferences_widgets, rootKey);
    }

}
