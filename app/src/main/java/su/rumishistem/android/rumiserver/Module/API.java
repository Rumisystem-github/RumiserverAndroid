package su.rumishistem.android.rumiserver.Module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class API {
	public static JsonNode RunGet(String Path) {
		try {
			HttpURLConnection Connection = OpenConnection(Path);
			Connection.setRequestMethod("GET");
			Connection.setRequestProperty("Accept", "application/json");

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			StringBuilder SB = new StringBuilder();
			BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "UTF-8"));
			String Line;
			while ((Line = BR.readLine()) != null) {
				SB.append(Line.trim());
			}

			return new ObjectMapper().readTree(SB.toString());
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	public static JsonNode RunPost(String Path, String Body) {
		try {
			HttpURLConnection Connection = OpenConnection(Path);
			Connection.setRequestMethod("POST");
			Connection.setDoOutput(true);
			Connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			Connection.setRequestProperty("Accept", "application/json");

			//JSONを送りつける
			OutputStream OS = Connection.getOutputStream();
			OS.write(Body.getBytes("UTF-8"));
			OS.flush();

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			StringBuilder SB = new StringBuilder();
			BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "UTF-8"));
			String Line;
			while ((Line = BR.readLine()) != null) {
				SB.append(Line.trim());
			}

			return new ObjectMapper().readTree(SB.toString());
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	public static byte[] GetIcon(String UID) {
		try {
			HttpURLConnection Connection = OpenConnection("Icon?UID=" + URLEncoder.encode(UID));
			Connection.setRequestMethod("GET");

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
			byte[] Temp = new byte[4096];
			int Length;
			while ((Length = IS.read(Temp)) != -1) {
				BAOS.write(Temp, 0, Length);
			}

			return BAOS.toByteArray();
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	private static HttpURLConnection OpenConnection(String Path) throws IOException {
		URL RequestURL = new URL(GetAPIHost.HTTP() + Path);
		System.out.println("HTTP:" + RequestURL.toString());

		HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
		return Connection;
	}
}
