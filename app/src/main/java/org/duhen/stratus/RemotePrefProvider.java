package org.duhen.stratus;

import android.net.Uri;
import android.os.Binder;
import android.os.ParcelFileDescriptor;

import com.crossbowffs.remotepreferences.RemotePreferenceFile;
import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

import java.io.FileNotFoundException;

public class RemotePrefProvider extends RemotePreferenceProvider {

    public static final String AUTHORITY = "org.duhen.stratus";
    public static final String PREF_FILE = "org.duhen.stratus_preferences";

    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    private static final String SYSTEM_SERVER_PACKAGE = "android";
    private static final String PACKAGE_INSTALLER_PACKAGE = "com.android.packageinstaller";
    private static final String SUGGESTION_PACKAGE = "com.meizu.suggestion";
    private static final String SETTINGS_PACKAGE = "com.android.settings";
    private static final String SETTINGS_BG_PATH = "settings/bg_system_upgrade";

    public RemotePrefProvider() {
        super(AUTHORITY, new RemotePreferenceFile[]{new RemotePreferenceFile(PREF_FILE, false)});
    }

    @Override
    public boolean checkAccess(String prefFileName, String prefKey, boolean write) {
        android.content.Context ctx = getContext();
        if (ctx == null) return false;

        String appPackage = ctx.getPackageName();
        String caller = getCallingPackage();

        if (caller != null) {
            if (caller.equals(appPackage)) return true;
            if (write) return false;
            return caller.equals(SYSTEMUI_PACKAGE)
                    || caller.equals(SYSTEM_SERVER_PACKAGE)
                    || caller.equals(PACKAGE_INSTALLER_PACKAGE)
                    || caller.equals(SUGGESTION_PACKAGE)
                    || caller.equals(SETTINGS_PACKAGE);
        }

        int callingUid = Binder.getCallingUid();
        String[] packages = ctx.getPackageManager().getPackagesForUid(callingUid);
        if (packages == null) return false;

        for (String pkg : packages) {
            if (pkg.equals(appPackage)) return true;
        }
        if (write) return false;
        for (String pkg : packages) {
            if (pkg.equals(SYSTEMUI_PACKAGE)
                    || pkg.equals(SYSTEM_SERVER_PACKAGE)
                    || pkg.equals(PACKAGE_INSTALLER_PACKAGE)
                    || pkg.equals(SUGGESTION_PACKAGE)
                    || pkg.equals(SETTINGS_PACKAGE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"r".equals(mode)) throw new FileNotFoundException("Read-only provider");
        String path = uri.getPath();
        if (path != null && path.startsWith("/")) path = path.substring(1);
        if (!SETTINGS_BG_PATH.equals(path)) throw new FileNotFoundException(uri.toString());

        android.content.Context ctx = getContext();
        if (ctx == null) throw new FileNotFoundException("No context");

        String caller = getCallingPackage();
        if (caller != null && !caller.equals(ctx.getPackageName()) && !caller.equals(SETTINGS_PACKAGE)) {
            throw new FileNotFoundException("Access denied: " + caller);
        }

        return ParcelFileDescriptor.open(
                SettingsBackgroundStore.getFile(ctx),
                ParcelFileDescriptor.MODE_READ_ONLY);
    }
}
