package org.duhen.stratus;

import android.content.Context;
import android.util.Log;

import java.io.File;

public final class PreferenceUtils {

    private PreferenceUtils() {}

    public static void fixPermissions(Context context) {
        try {
            String fileName = context.getPackageName() + "_preferences.xml";
            File dataDir  = new File(context.getApplicationInfo().dataDir);
            File prefsDir = new File(dataDir, "shared_prefs");
            File prefsFile = new File(prefsDir, fileName);

            if (prefsDir.exists()) {
                prefsDir.setReadable(true, false);
                prefsDir.setExecutable(true, false);
            }
            if (prefsFile.exists()) {
                boolean ok = prefsFile.setReadable(true, false);
                if (!ok) Log.w("Stratus", "Failed to set world readable: " + prefsFile);
                else     Log.d("Stratus", "Fixed permissions for: " + prefsFile);
            }

            try {
                File userDeFile = new File(
                        "/data/user_de/0/" + context.getPackageName()
                        + "/shared_prefs/" + fileName);
                if (userDeFile.exists()) userDeFile.setReadable(true, false);
            } catch (Exception ignored) {}

        } catch (Exception e) {
            Log.e("Stratus", "Error fixing permissions", e);
        }
    }
}
