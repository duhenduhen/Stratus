package org.duhen.stratus;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public final class SettingsBackgroundStore {

    public static final String FILE_NAME = "bg_system_upgrade.png";
    public static final Uri CONTENT_URI = Uri.parse(
            "content://org.duhen.stratus/settings/bg_system_upgrade");
    private static final String DIR_NAME = "settings";

    private SettingsBackgroundStore() {}

    public static File getFile(Context context) {
        Context deContext = context.createDeviceProtectedStorageContext();
        return new File(new File(deContext.getFilesDir(), DIR_NAME), FILE_NAME);
    }

    public static void save(Context context, Uri uri) throws Exception {
        File file = getFile(context);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs() && !dir.exists()) {
            throw new IllegalStateException("Failed to create " + dir);
        }

        try (InputStream in = context.getContentResolver().openInputStream(uri);
             OutputStream out = new java.io.FileOutputStream(file)) {
            if (in == null) throw new IllegalStateException("Unable to open selected image");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
        }

        relax(context.createDeviceProtectedStorageContext().getDataDir());
        relax(dir);
        file.setReadable(true, false);
    }

    public static boolean exists(Context context) {
        return getFile(context).exists();
    }

    public static boolean delete(Context context) {
        File file = getFile(context);
        return !file.exists() || file.delete();
    }

    private static void relax(File file) {
        if (file == null || !file.exists()) return;
        file.setReadable(true, false);
        file.setExecutable(true, false);
    }
}
