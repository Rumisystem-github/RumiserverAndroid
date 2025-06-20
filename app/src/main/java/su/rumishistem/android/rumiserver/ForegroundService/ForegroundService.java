package su.rumishistem.android.rumiserver.ForegroundService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import su.rumishistem.android.rumiserver.IForegrondService;
import su.rumishistem.android.rumiserver.Module.API;
import su.rumishistem.android.rumiserver.Module.TokenManager;

public class ForegroundService extends Service {
	private static final String ChannelID = "rumiserver_notify_channel";
	private static final String LogTag = "RSVBG";

	private static ForegroundService Context;
	private WebSocketService WSS = null;
	protected boolean Destroy = false;

	//private final IForegrondService.Stub Binder = new IPCService(this);

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

		//WebSocket
		WSS = new WebSocketService();
		WSS.Start(Context);

		try {
			new HTTPServer(3000, Context).start();
		} catch (Exception EX) {
			EX.printStackTrace();
		}

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
}
