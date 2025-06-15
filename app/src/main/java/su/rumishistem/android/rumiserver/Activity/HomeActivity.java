package su.rumishistem.android.rumiserver.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.android.rumiserver.IForegrondService;
import su.rumishistem.android.rumiserver.Module.UserIconManager;
import su.rumishistem.android.rumiserver.R;

public class HomeActivity extends AppCompatActivity {
	private HomeActivity Context;
	private IForegrondService AIDLInterface;

	private ServiceConnection AIDL = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName Name, IBinder iBinder) {
			AIDLInterface = IForegrondService.Stub.asInterface(iBinder);

			try {
				JsonNode User = new ObjectMapper().readTree(AIDLInterface.getSelf());

				ImageView user_icon_imageview = findViewById(R.id.user_icon_imageview);
				user_icon_imageview.setImageBitmap(UserIconManager.Get(User.get("UID").asText()));

				TextView user_name_textview = findViewById(R.id.user_name_textview);
				user_name_textview.setText(User.get("NAME").asText());
			} catch (Exception EX) {
				EX.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName Name) {
			AIDLInterface = null;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();

		Intent AIDLIntent = new Intent("su.rumishistem.android.rumiserver.AIDL");
		AIDLIntent.setPackage("su.rumishistem.android.rumiserver");
		bindService(AIDLIntent, AIDL, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(AIDL);
	}

	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		Context = this;

		//UI
		setContentView(R.layout.home_activity);
	}
}
