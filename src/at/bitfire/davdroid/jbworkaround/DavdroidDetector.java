package at.bitfire.davdroid.jbworkaround;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class DavdroidDetector {
	public final static String
		PACKAGE_NAME_DAVDROID = "at.bitfire.davdroid",
		PACKAGE_NAME_GOOGLE_PLAY = "com.android.vending";
	
	
	static boolean installed; 
	static String installerPackageName, installerName;

	
	public static void detect(PackageManager packageManager) {
		try {
			installerPackageName = packageManager.getInstallerPackageName(PACKAGE_NAME_DAVDROID);
			installed = true;
			
			if (installerPackageName != null)
				try {
					ApplicationInfo installerInfo = packageManager.getApplicationInfo(installerPackageName, 0);
					installerName = packageManager.getApplicationLabel(installerInfo).toString();
				} catch (NameNotFoundException e) {
					installerName = "?" + installerPackageName;
				}
		} catch(IllegalArgumentException e) {
			installed = false;
			return;
		}
	}
	
	public static boolean fromGooglePlay() {
		return PACKAGE_NAME_GOOGLE_PLAY.equals(installerPackageName);
	}

}
