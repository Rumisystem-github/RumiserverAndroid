package su.rumishistem.android.rumiserver.Module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class IPCHTTP {
	public static String getToken() {
		try {
			JsonNode Body = GET("Token");
			if (Body.get("STATUS").asBoolean()) {
				return Body.get("TOKEN").asText();
			} else {
				throw new Error("トークンを取得できませんでした");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("トークンを取得できませんでした");
		}
	}

	public static JsonNode getSelf() {
		try {
			JsonNode Body = GET("Self");
			if (Body.get("STATUS").asBoolean()) {
				return Body.get("SELF");
			} else {
				throw new Error("トークンを取得できませんでした");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("トークンを取得できませんでした");
		}
	}

	private static JsonNode GET(String Path) throws IOException {
		URL RequestURL = new URL("http://localhost:3000/" + Path);
		System.out.println("HTTP:" + RequestURL.toString());

		HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
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
	}
}
