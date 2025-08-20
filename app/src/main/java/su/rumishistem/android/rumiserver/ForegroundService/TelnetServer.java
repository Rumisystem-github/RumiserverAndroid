package su.rumishistem.android.rumiserver.ForegroundService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.os.Build;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import su.rumishistem.android.rumiserver.Module.AuthManager;

public class TelnetServer {
	public static final String AUTH_DIALOG_RESULT_INTENT = "su.rumishistem.AUTH_RESULT";

	private static ExecutorService es = Executors.newFixedThreadPool(10);
	private static ServerSocket socket;

	public static void start(ForegroundService parent, Service ctx) throws IOException, InterruptedException {
		socket = new ServerSocket(45451, 50, InetAddress.getByName("127.0.0.1"));
		System.out.println("Socket起動");

		while (!socket.isClosed()) {
			Socket session = socket.accept();
			es.submit(new Runnable() {
				@Override
				public void run() {
					String[] package_name = {null};

					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(session.getInputStream()));
						PrintWriter out = new PrintWriter(session.getOutputStream(), true);

						String line;
						while ((line = in.readLine()) != null) {
							String[] cmd = line.split(" ");

							switch (cmd[0]) {
								case "HELO": {
									if (cmd.length != 2) {
										out.println("400");
										break;
									}

									package_name[0] = cmd[1];
									out.println("200");
									break;
								}

								case "AUTH": {
									System.out.println("ログイン申請");

									//↓受け取り処理
									LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(ctx);
									BroadcastReceiver receiver = new BroadcastReceiver() {
										@Override
										public void onReceive(Context ctx, Intent intent) {
											boolean result = intent.getBooleanExtra("RESULT", false);
											String[] status = {"NG"};
											String[] token = {""};

											if (result) {
												status[0] = "OK";
												//token[0] = AuthManager.regist(ctx, package_name[0]);
												token[0] = parent.getToken();
											}

											new Thread(new Runnable() {
												@Override
												public void run() {
													out.println("200 <"+status[0]+"> <"+token[0]+">");
												}
											}).start();
										}
									};
									lbm.registerReceiver(receiver, new IntentFilter(AUTH_DIALOG_RESULT_INTENT));

									Intent intent = new Intent(ctx.getApplication(), AuthDialog.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.putExtra("PKG", package_name[0]);
									ctx.getApplication().startActivity(intent);
									break;
								}

								default: {
									out.println("400");
									break;
								}
							}
						}
					} catch (Exception ex) {
						try {
							session.close();
						} catch (IOException e) {
							//無視
						}
					}
				}
			});
		}
	}
}
