package su.rumishistem.android.rumiserver.ForegroundService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AuthDialog extends Activity {
	@Override
	protected void onCreate(@Nullable Bundle saved_instance_state) {
		super.onCreate(saved_instance_state);
		Intent intent = getIntent();
		String package_name = intent.getStringExtra("PKM");
		Activity ctx = this;
		String app_name = "不明";

		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo app_info = pm.getApplicationInfo(package_name, 0);
			app_name = pm.getApplicationLabel(app_info).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(app_name + "がログインしようとしています");
		dialog.setMessage("あなたのアカウントへのアクセスを許可しますか？");
		dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog_interface, int i) {
				Intent intent = new Intent(TelnetServer.AUTH_DIALOG_RESULT_INTENT);
				intent.putExtra("RESULT", true);
				LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

				finish();
			}
		});
		dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog_interface, int i) {
				Intent intent = new Intent(TelnetServer.AUTH_DIALOG_RESULT_INTENT);
				intent.putExtra("RESULT", false);
				LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

				finish();
			}
		});
		dialog.setCancelable(false);
		dialog.show();
	}
}
