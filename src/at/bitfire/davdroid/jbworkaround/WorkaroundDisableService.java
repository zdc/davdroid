/*
 * Copyright (C) 2012 Marten Gajda <marten@dmfs.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package at.bitfire.davdroid.jbworkaround;

import java.util.Timer;
import java.util.TimerTask;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class WorkaroundDisableService extends Service
{
	private final static String TAG = "davdroid.workaround.WorkaroundDisableService";
	private final static Timer mTimer = new Timer();


	@Override
	public void onCreate()
	{
		super.onCreate();

		String	realPackage = getString(R.string.package_name),
				accountType = getString(R.string.account_type),		
				usedPackage = getPackageForAccount(WorkaroundDisableService.this, accountType);
		
		Log.i(TAG, "Package " + usedPackage + " used for " + accountType + " (real package: " + realPackage + ")");
		
		if (!realPackage.equals(usedPackage))
		{
			// the account is not managed by the real authenticator, we have to run the checker task
			mTimer.scheduleAtFixedRate(new mCheckerTask(), 2500, 2500);
		}
		else
		{
			// everything is in place, nothing to do
			Log.i(TAG, "not starting because everything is fine");
			stopSelf();
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// explicitly return START_STICKY
		return START_STICKY;
	}


	@Override
	public IBinder onBind(Intent intent)
	{
		// no binding allowed
		return null;
	}

	
	private class mCheckerTask extends TimerTask
	{
		public void run()
		{
			Log.i(TAG, "Checking accounts");

			Context context = WorkaroundDisableService.this;
			String	realPackage = getString(R.string.package_name),
					accountType = getString(R.string.account_type);

			try {
				context.getPackageManager().getApplicationInfo(realPackage, 0);
			} catch (PackageManager.NameNotFoundException ex) {
				// original package not found yet, nothing to do
				Log.i(TAG, "Real package " + realPackage + " not present (yet).", ex);
				return;
			}

			// The original authenticator package has been found, disable the workaround authenticator of not already done.
			PackageManager pm = getPackageManager();
			ComponentName authenticatorComponent = new ComponentName(context, AuthenticationService.class);
			if (pm.getComponentEnabledSetting(authenticatorComponent) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
			{
				// the fake authenticator is not disabled yet, do that now to let the real authenticator take over
				Log.i(TAG, "Disabling workaround authenticator");
				pm.setComponentEnabledSetting(new ComponentName(context, AuthenticationService.class),
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

				// we can't do anything else here, give the account manager some time to take notice of the change
				return;
			}

			// fake authenticator is disabled now. All we can do is to wait for the original authenticator to take over
			String usedPackage = getPackageForAccount(WorkaroundDisableService.this, accountType);
			Log.i(TAG, "Package " + usedPackage + " is currently used for " + accountType);
			if (realPackage.equals(usedPackage))
			{
				// re-enable the workaround now that the original authenticator has taken over
				Log.i(TAG, "Re-enabling workaround authenticator");
				pm.setComponentEnabledSetting(authenticatorComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

				// stop this service, we're done
				mTimer.cancel();
				WorkaroundDisableService.this.stopSelf();
			}
		}
	}


	private static String getPackageForAccount(Context context, String accountType)
	{
		AccountManager am = AccountManager.get(context);
		AuthenticatorDescription[] authenticators = am.getAuthenticatorTypes();
		// check all authenticators for the given package name
		for (AuthenticatorDescription authenticator : authenticators)
			if (authenticator.type.equals(accountType))
				return authenticator.packageName;
		return null;
	}
}
