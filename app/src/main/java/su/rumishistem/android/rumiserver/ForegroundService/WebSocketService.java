package su.rumishistem.android.rumiserver.ForegroundService;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import su.rumishistem.android.rumiserver.Module.GetAPIHost;

public class WebSocketService extends WebSocketListener {
	private static final String HandshakeID = "1";
	public WebSocket SESSION = null;
	private boolean Handshake = false;
	private ForegroundService FS = null;

	public void Start(ForegroundService FS) {
		this.FS = FS;
		this.Handshake = false;

		OkHttpClient Client = new OkHttpClient();
		Request r = new Request.Builder().url(GetAPIHost.WS()).build();
		SESSION = Client.newWebSocket(r, this);

		System.out.println("接続");
	}

	@Override
	public void onOpen(@NonNull WebSocket S, @NonNull Response r) {
		System.out.println("はんどしぇーく");
		SESSION.send("[\""+HandshakeID+"\", \"HELO\", \""+FS.getToken()+"\"]");
	}

	@Override
	public void onMessage(@NonNull WebSocket S, @NonNull String Text) {
		System.out.println(Text);

		try {
			JsonNode Message = new ObjectMapper().readTree(Text);

			if (!Handshake) {
				if (Message.get("REQUEST").asText().equals(HandshakeID)) {
					Handshake = true;
				}
				return;
			}

			switch (Message.get("TYPE").asText()) {
				case "NOTIFY":
					JsonNode Notify = Message.get("DATA");
					FS.CreateNotify(Notify.get("SERVICE").asText(), Notify.get("TITLE").asText());
					break;
			}
		} catch (Exception EX) {
			//あ
		}
	}

	@Override
	public void onClosing(@NonNull WebSocket S, int code, @NonNull String Reason) {
		Log.i("aaaaaaaaaaa", "切断");
		if (!FS.Destroy) {
			Start(FS);
		}
	}

	@Override
	public void onFailure(@NonNull WebSocket S, @NonNull Throwable T, @Nullable Response r) {
		Start(FS);
	}
}
