
package at.bitfire.davdroid.jbworkaround;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class AuthenticationService extends Service
{
	private final static String TAG = "davdroid.workaround.AuthenticationService";
	private Authenticator mAuthenticator;

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(TAG, "Service has been created");
		
		mAuthenticator = new Authenticator(this);

		Intent serviceIntent = new Intent(this, WorkaroundDisableService.class);
		startService(serviceIntent);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return mAuthenticator.getIBinder();
	}
}
