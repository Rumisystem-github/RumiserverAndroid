package su.rumishistem.android.rumiserver.ForegroundService;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLEncoder;
import su.rumishistem.android.rumiserver.Module.API;
import su.rumishistem.android.rumiserver.Module.TokenManager;
import su.rumishistem.android.rumiserver.R;

public class ForegroundService extends Service {
	private static final String ChannelID = "rumiserver_notify_channel";
	private static final String LogTag = "RSVBG";

	private static ForegroundService Context;
	private WebSocketService WSS = null;
	protected boolean Destroy = false;

	@Override
	public IBinder onBind(Intent INT) {
		//return Binder;
		return null;
	}

	@Override
	public void onCreate() {
		//起動した
		super.onCreate();
		Context = this;

		CreateNotifyChannel();

		Notification Notify = new NotificationCompat.Builder(this, ChannelID)
			.setContentTitle("るみさーばー")
			.setContentText("常駐しました。")
			.setSmallIcon(android.R.drawable.stat_notify_sync)
			.build();

		startForeground(1, Notify);

		Log.i(LogTag, "常駐が起動しました！");
	}

	@Override
	public void onDestroy() {
		//終了処理
		Destroy = true;
		WSS.SESSION.close(1000, "Close");

		System.out.println("終了処理中");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent INT, int Flag, int StartID) {
		//常駐処理
		Service ctx = this;
		ForegroundService parent = this;

		//WebSocket
		WSS = new WebSocketService();
		WSS.Start(Context);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//new HTTPServer(3000, Context).start();
					TelnetServer.start(parent, ctx);
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}
		}).start();

		return START_STICKY;
	}

	protected String getToken() {
		SharedPreferences PREF = this.getSharedPreferences(TokenManager.PrefName, MODE_PRIVATE);
		return TokenManager.getToken(PREF);
	}

	public JsonNode getSession() {
		JsonNode Result = API.RunGet("Session?ID=" + URLEncoder.encode(getToken()), null);
		return Result;
	}

	protected void CreateNotify(String Title, String Text) {
		NotificationManager NM = (NotificationManager) Context.getSystemService(NOTIFICATION_SERVICE);

		NotificationCompat.Builder Builder = new NotificationCompat.Builder(Context, ChannelID);
		Builder.setContentTitle(Title);
		Builder.setContentText(Text);
		Builder.setSmallIcon(android.R.drawable.stat_notify_sync);

		NM.notify(2, Builder.build());
	}

	private void CreateNotifyChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel ServiceChannel = new NotificationChannel(ChannelID, "るみ鯖", NotificationManager.IMPORTANCE_LOW);
			NotificationManager Manager = getSystemService(NotificationManager.class);
			Manager.createNotificationChannel(ServiceChannel);
		}
	}

	public void show_auth_notify(String package_name) {
		//通知
		String channel_id = "auth_notify";
		NotificationManager nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel nc = new NotificationChannel(
					channel_id,
					"認証通知",
					NotificationManager.IMPORTANCE_HIGH
			);
			nm.createNotificationChannel(nc);
		}

		//インテント作成
		Intent intent = new Intent(this.getApplication(), AuthDialog.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("PKG", package_name);
		PendingIntent pending_intent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

		//通知発行
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel_id);
		builder.setContentTitle("ログイン要求");
		builder.setContentText("タップして認証してください");
		builder.setSmallIcon(R.drawable.ic_launcher_background);
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);
		builder.setContentIntent(pending_intent);
		builder.setAutoCancel(true);

		NotificationManagerCompat.from(this).notify(1, builder.build());
	}
}
