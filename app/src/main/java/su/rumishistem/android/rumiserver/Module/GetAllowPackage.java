package su.rumishistem.android.rumiserver.Module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetAllowPackage {
	public static boolean isAllow(String PackageName) {
		try {
			URL RequestURL = new URL(getHost());
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

			JsonNode Result = new ObjectMapper().readTree(SB.toString()).get("ANDROID");
			for (int I = 0; I < Result.size(); I++) {
				if (Result.get(I).asText().equals(PackageName)) {
					return true;
				}
			}

			return false;
		} catch (Exception EX) {
			EX.printStackTrace();
			return false;
		}
	}

	private static String getHost() {
		try {
			//外部
			URL RequestURL = new URL("https://rumiserver.com/ALLOW_PACKAGE.json");
			HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
			Connection.getResponseCode();

			return "https://rumiserver.com/ALLOW_PACKAGE.json";
		} catch (Exception EX) {
			//ローカル
			return "http://192.168.0.3:8080/ALLOW_PACKAGE.json";
		}
	}
}
