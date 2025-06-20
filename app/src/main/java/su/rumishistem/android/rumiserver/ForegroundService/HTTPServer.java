package su.rumishistem.android.rumiserver.ForegroundService;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;

import su.rumishistem.android.rumiserver.Module.API;

public class HTTPServer extends Thread{
	private ServerSocket SS;
	private ForegroundService FS;

	public HTTPServer(int Port, ForegroundService Context) throws IOException {
		SS = new ServerSocket(Port);
		FS = Context;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket Session = SS.accept();
				BufferedReader In = new BufferedReader(new InputStreamReader(Session.getInputStream()));
				PrintWriter Out = new PrintWriter(Session.getOutputStream());

				//リクエスト(GET / HTTP/1.1)
				String RequestLine = In.readLine();
				if (RequestLine == null || RequestLine.isEmpty()) {
					Session.close();
					continue;
				}

				//ヘッダー
				String Line;
				HashMap<String, String> HeaderList = new HashMap<>();
				while ((Line = In.readLine()) != null && !Line.isEmpty()) {
					int Colon = Line.indexOf(":");
					if (Colon != -1) {
						String Key = Line.substring(0, Colon).trim();
						String Val = Line.substring(Colon + 1).trim();
						HeaderList.put(Key, Val);
					}
				}

				String Path = RequestLine.split(" ")[1];
				String Body = null;

				System.out.println("HTTP:" + Path);

				switch (Path) {
					case "/Token": {
						Body = "{\"STATUS\": true, \"TOKEN\": \""+FS.getToken()+"\"}";
						break;
					}

					case "/Self": {
						LinkedHashMap<String, Object> Self = new LinkedHashMap<>();
						Self.put("STATUS", true);
						Self.put("SELF", FS.getSession().get("ACCOUNT_DATA"));
						Body = new ObjectMapper().writeValueAsString(Self);
						break;
					}

					default: {
						Body = "{\"STATUS\": false}";
					}
				}

				Out.println("HTTP/1.1 200 OK");
				Out.println("Content-Type: application/json; charset=UTF-8");
				Out.println("Content-Length: " + Body.getBytes().length);
				Out.println("Connection: close");
				Out.println();
				Out.println(Body);
				Out.flush();

				Session.close();
			} catch (Exception EX) {
				EX.printStackTrace();
			}
		}
	}
}
