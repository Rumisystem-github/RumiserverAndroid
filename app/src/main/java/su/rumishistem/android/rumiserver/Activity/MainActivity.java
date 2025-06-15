package su.rumishistem.android.rumiserver.Activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import su.rumishistem.android.rumiserver.ForegroundService.ForegroundService;
import su.rumishistem.android.rumiserver.Module.TokenManager;
import su.rumishistem.android.rumiserver.Module.UserIconManager;
import su.rumishistem.android.rumiserver.R;

public class MainActivity extends AppCompatActivity {
	private MainActivity Context;

	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		Context = this;

		//UI
		getSupportActionBar().hide();
		setContentView(R.layout.main_activity);

		SharedPreferences PREF = Context.getSharedPreferences(TokenManager.PrefName, Context.MODE_PRIVATE);

		//初回起動時か？
		if (!TokenManager.isLogin(PREF)) {
			Intent LoginIntent = new Intent(Context, WelcomeActivity.class);
			LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(LoginIntent);
			finish();
			return;
		}

		//サービスが起動して居なければ起動する
		if (!isServiceRunning(ForegroundService.class)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				ContextCompat.startForegroundService(Context, new Intent(this, ForegroundService.class));
			} else {
				startService(new Intent(this, ForegroundService.class));
			}
		}

		//初期化
		UserIconManager.Init();

		//ホーム画面を開く
		Intent HomeIntent = new Intent(Context, HomeActivity.class);
		HomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(HomeIntent);
		finish();
	}

	//サービスが起動しているかチェック
	private boolean isServiceRunning(Class<?> ServiceClass) {
		ActivityManager Manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo Service:Manager.getRunningServices(Integer.MAX_VALUE)) {
			if (ServiceClass.getName().equals(Service.service.getClassName())) {
				return true;
			}
		}

		return false;
	}
}
