package su.rumishistem.android.rumiserver.Activity.Home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.CountDownLatch;

import su.rumishistem.android.rumiserver.Activity.ProfileEditor;
import su.rumishistem.android.rumiserver.IForegrondService;
import su.rumishistem.android.rumiserver.Module.IPCHTTP;
import su.rumishistem.android.rumiserver.Module.UserIconManager;
import su.rumishistem.android.rumiserver.R;

public class HomeActivity extends AppCompatActivity {
	private HomeActivity Context = this;
	private DrawerLayout DL;
	private ActionBarDrawerToggle Toggle;
	private ActivityResultLauncher<Intent> ResultLauncher;

	private JsonNode SelfUser;
	private String Token;

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		//UI
		setContentView(R.layout.home_activity);

		//戻ってきた時用のやつ
		ResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
			@Override
			public void onActivityResult(ActivityResult Result) {
				if (Result.getResultCode() == RESULT_OK) {
					Intent Data = Result.getData();
					//ShowToast("おかえり");
				}
			}
		});

		Toolbar TB = findViewById(R.id.HomeToolbar);
		setSupportActionBar(TB);

		DL = findViewById(R.id.HomeDrawableLayout);
		NavigationView NavView = findViewById(R.id.HomeNavView);

		Toggle = new ActionBarDrawerToggle(
			Context,
			DL,
			TB,
			R.string.open,
			R.string.close
		);
		DL.addDrawerListener(Toggle);
		Toggle.syncState();

		NavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
				androidx.fragment.app.Fragment Fragment = null;
				int ItemID = Item.getItemId();

				if (ItemID == R.id.homeTopActivity) {
					Fragment = new TopFragment(Context);
				} else if (ItemID == R.id.homeSettingActivity) {
					Fragment = new SettingFragment(Context);
				}

				if (Fragment != null) {
					ChangeFragment(Fragment);
				}

				DL.closeDrawer(GravityCompat.START);
				return true;
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				SelfUser = IPCHTTP.getSelf();
				Token = IPCHTTP.getToken();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//ナビゲーションメニューのヘッダーを取得
						NavigationView NavView = findViewById(R.id.HomeNavView);
						View HeaderView = NavView.getHeaderView(0);

						//アイコン
						ImageView UserIconView = HeaderView.findViewById(R.id.user_icon_imageview);
						UserIconView.setImageBitmap(UserIconManager.Get(SelfUser.get("UID").asText()));

						//名前
						TextView UserNameView = HeaderView.findViewById(R.id.user_name_textview);
						UserNameView.setText(SelfUser.get("NAME").asText());

						ChangeFragment(new TopFragment(Context));
					}
				});
			}
		}).start();
	}

	private void ChangeFragment(androidx.fragment.app.Fragment Fragment) {
		if (Fragment == null) throw new Error("フラグメントがNull");

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, Fragment)
				.commit();
	}

	public JsonNode getSelfUser() {
		return SelfUser;
	}

	public String getToken() {
		return Token;
	}

	public void ShowToast(String Text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Context, Text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void OpenActivity(Class a) {
		try {
			Intent I = new Intent(Context, a);
			I.putExtra("TOKEN", getToken());
			I.putExtra("SELF", new ObjectMapper().writeValueAsString(getSelfUser()));
			ResultLauncher.launch(I);
		} catch (Exception EX) {
			ShowToast("エラー");
		}
	}
}
