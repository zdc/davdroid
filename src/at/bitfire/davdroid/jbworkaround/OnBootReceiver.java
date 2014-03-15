package at.bitfire.davdroid.jbworkaround;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {
	private static final String TAG = "davdroid.workaround.OnBootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Enabling WorkaroundDisableService");
		Intent serviceIntent = new Intent(context, WorkaroundDisableService.class);
        context.startService(serviceIntent);
	}
}
