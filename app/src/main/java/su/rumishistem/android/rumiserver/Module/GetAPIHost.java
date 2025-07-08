package su.rumishistem.android.rumiserver.Module;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAPIHost {
	private static final String HTTPHost = "https://account.rumiserver.com/api/";
	private static final String HTTPLocalHost = "http://192.168.0.125:3000/";

	private static final String WSHost = "wss://account.rumiserver.com/api/ws";
	private static final String WSLocalHost = "ws://192.168.0.125:3002/";

	public static String HTTP() {
		try {
			//外部
			URL RequestURL = new URL(HTTPHost);
			HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
			Connection.getResponseCode();

			return HTTPHost;
		} catch (Exception EX) {
			//ローカル
			return HTTPLocalHost;
		}
	}

	public static String WS() {
		try {
			//外部
			URL RequestURL = new URL(WSHost);
			HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
			Connection.getResponseCode();

			return WSHost;
		} catch (Exception EX) {
			//ローカル
			return WSLocalHost;
		}
	}
}
