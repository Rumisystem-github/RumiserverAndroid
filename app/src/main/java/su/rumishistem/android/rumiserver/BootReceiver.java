package su.rumishistem.android.rumiserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import su.rumishistem.android.rumiserver.ForegroundService.ForegroundService;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.d("RumiServer", "バックグラウンドサービスを起動します...");

			Intent service_intent = new Intent(ctx, ForegroundService.class);
			ctx.startService(service_intent);
		}
	}
}
