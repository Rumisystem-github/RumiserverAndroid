package su.rumishistem.android.rumiserver.Module;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class IPCHTTP {
	private static final String telnet_host = "localhost";
	private static final int telnet_port = 45451;

	public static String getToken(Context ctx) {
		try {
			/*Socket socket = new Socket(telnet_host, telnet_port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			out.println("HELO su.rumishistem.android.rumiserver");
			String l1 = in.readLine();
			if (l1 == null) throw new Error("トークンを取得できませんでした");
			if (!l1.startsWith("200")) throw new Error("トークンを取得できませんでした");

			out.println("AUTH");
			String l2 = in.readLine();
			if (l2 == null) throw new Error("トークンを取得できませんでした");
			if (!l2.startsWith("200")) throw new Error("トークンを取得できませんでした");*/

			return TokenManager.getToken(ctx);
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("トークンを取得できませんでした");
		}
	}

	public static JsonNode getSelf(Context ctx) {
		try {
			return API.RunGet("Session?ID="+getToken(ctx), getToken(ctx)).get("ACCOUNT_DATA");
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("トークンを取得できませんでした");
		}
	}
}
