package org.duhen.stratus;

import android.content.Context;

import java.io.File;

public final class PrefsUtil {

    private PrefsUtil() {
    }

    public static void ensurePrefsAccessible(Context context) {
        makeWorldReadable(context);
        syncToDeviceProtected(context);
    }

    public static void makeWorldReadable(Context context) {
        try {
            File dataDir = new File(context.getApplicationInfo().dataDir);
            File prefsDir = new File(dataDir, "shared_prefs");
            File file = new File(prefsDir, context.getPackageName() + "_preferences.xml");

            relaxDir(dataDir);
            File parent = dataDir.getParentFile();
            relaxDir(parent != null ? parent : new File("/data/user/0"));
            if (prefsDir.exists()) relaxDir(prefsDir);
            if (file.exists()) {
                file.setReadable(true, false);
                file.setWritable(true, false);
            }
        } catch (Throwable ignored) {
        }
    }

    public static void syncToDeviceProtected(Context context) {
        try {
            Context deContext = context.createDeviceProtectedStorageContext();
            File cePrefs = new File(
                    context.getApplicationInfo().dataDir + "/shared_prefs/"
                            + context.getPackageName() + "_preferences.xml");
            File deDir = new File(deContext.getDataDir(), "shared_prefs");
            File dePrefs = new File(deDir, context.getPackageName() + "_preferences.xml");

            if (!deDir.exists() && !deDir.mkdirs() && !deDir.exists()) return;

            relaxDir(deContext.getDataDir());
            File deParent = deContext.getDataDir().getParentFile();
            relaxDir(deParent != null ? deParent : new File("/data/user_de/0"));
            relaxDir(deDir);

            if (cePrefs.exists()) {
                copyFile(cePrefs, dePrefs);
                dePrefs.setReadable(true, false);
                dePrefs.setWritable(true, false);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void relaxDir(File dir) {
        if (dir == null || !dir.exists()) return;
        dir.setReadable(true, false);
        dir.setExecutable(true, false);
    }

    private static void copyFile(File src, File dst) throws Exception {
        try (java.io.InputStream in = new java.io.FileInputStream(src);
             java.io.OutputStream out = new java.io.FileOutputStream(dst)) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
        }
    }
}
