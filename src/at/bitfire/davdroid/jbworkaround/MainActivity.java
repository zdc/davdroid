package at.bitfire.davdroid.jbworkaround;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	@Override
	protected void onResume() {
		super.onResume();
		
		DavdroidDetector.detect(getPackageManager());
		
		// prepare text views
		setContentView(R.layout.activity_main);
		
		TextView text = (TextView)findViewById(R.id.info_text);
		text.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView textCredits = (TextView)findViewById(R.id.credits_text);
		textCredits.setText(Html.fromHtml(getString(R.string.credits)));
		textCredits.setMovementMethod(LinkMovementMethod.getInstance());
		
		Button	btnGetDavdroid = (Button)findViewById(R.id.get_davdroid),
				btnUninstallWorkaround = (Button)findViewById(R.id.uninstall_davdroid_jb_workaround);
	
		if (!DavdroidDetector.installed) {
			// DAVdroid not installed
			text.setText(Html.fromHtml(getString(R.string.davdroid_not_installed)));
			btnGetDavdroid.setVisibility(View.VISIBLE);
			btnUninstallWorkaround.setVisibility(View.VISIBLE);
			
		} else if (DavdroidDetector.fromGooglePlay()) {
			// DAVdrod installed from Play Store
	        text.setText(Html.fromHtml(getString(R.string.workaround_info_html)));
	
			// activate workaround service
			Intent workaroundIntent = new Intent("at.bitfire.davdroid.jbworkaround.INIT");
			workaroundIntent.setPackage(getString(R.string.jb_bug_workaround_app_package));
			startService(workaroundIntent);
			
		} else {
			// DAVdroid installed, but not from Play Store
			text.setText(Html.fromHtml(getString(
					R.string.davdroid_not_from_play_store,
					(DavdroidDetector.installerName != null) ? DavdroidDetector.installerName : getString(R.string.external_apk)
			)));
			btnUninstallWorkaround.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	public void getDavdroid(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://davdroid.bitfire.at/download?pk_campaign=jb-workaround-app"));
		startActivity(intent);
	}
	
	public void uninstallWorkaround(View v) {
		Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}
	
}
