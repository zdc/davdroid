
package at.bitfire.davdroid.jbworkaround;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


public class ShutdownReceiver extends BroadcastReceiver
{
	private final static String TAG = "davdroid.workaround";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent serviceIntent = new Intent(context, WorkaroundDisableService.class);
		context.stopService(serviceIntent);

		/*
		 * Re-enable fake authenticator for next boot.
		 * 
		 * Note: there might be a race condition if the checker task is still running. It might disable the authenticator again.
		 * 
		 * TODO: take care of this issue
		 */
		PackageManager pm = context.getPackageManager();
		ComponentName authenticatorComponent = new ComponentName(context, AuthenticationService.class);
		Log.i(TAG, "Re-enabling workaround authenticator");
		pm.setComponentEnabledSetting(authenticatorComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}
}
