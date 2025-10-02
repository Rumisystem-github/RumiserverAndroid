package su.rumishistem.android.rumiserver.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

import su.rumishistem.android.rumiserver.Module.API;
import su.rumishistem.android.rumiserver.Module.TokenManager;
import su.rumishistem.android.rumiserver.R;

public class LoginActivity extends AppCompatActivity {
	private LoginActivity Context;

	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		Context = this;

		//UI
		setContentView(R.layout.login_activity);

		TextInputEditText UserIDInput = findViewById(R.id.userid_input);
		TextInputEditText PasswordInput = findViewById(R.id.password_input);
		Button LoginButton = findViewById(R.id.login_button);

		LoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View V) {
				String UserID = UserIDInput.getText().toString();
				String Password = PasswordInput.getText().toString();
				Login(UserID, Password, null);
			}
		});
	}

	private void Login(String UID, String PASS, String TOTP) {
		HashMap<String, String> PostBody = new HashMap<>();
		PostBody.put("UID", UID);
		PostBody.put("PASS", PASS);
		PostBody.put("TOTP", TOTP);

		Handler H = new Handler(Looper.getMainLooper());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JsonNode Result = API.RunPost("Session", new ObjectMapper().writeValueAsString(PostBody), null);

					//失敗
					if (!Result.get("STATUS").asBoolean()) {
						H.post(new Runnable() {
							@Override
							public void run() {
								new AlertDialog.Builder(Context)
									.setTitle("エラー")
									.setMessage("サーバーでエラーが発生しました")
									.setPositiveButton("OK", null)
									.show();
							}
						});
						return;
					}

					if (Result.get("SESSION_ID").isNull()) {
						if (Result.get("REQUEST").asText().equals("TOTP")) {
							EditText Input = new EditText(Context);
							Input.setInputType(InputType.TYPE_CLASS_NUMBER);
							Input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
							Input.setHint("例:123456");

							//数字意外を受け付けない
							Input.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

							H.post(new Runnable() {
								@Override
								public void run() {
									AlertDialog.Builder Builder = new AlertDialog.Builder(Context);
									Builder.setTitle("TOTPを入力して");
									Builder.setView(Input);
									Builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface Dialog, int Which) {
											String Code = Input.getText().toString();
											H.post(new Runnable() {
												@Override
												public void run() {
													Login(UID, PASS, Code);
												}
											});
										}
									});
									Builder.setNegativeButton("キャンセル", null);
									Builder.show();
								}
							});
						} else {
							H.post(new Runnable() {
								@Override
								public void run() {
									new AlertDialog.Builder(Context)
										.setTitle("エラー")
										.setMessage("意味分からん物を要求してきました")
										.setPositiveButton("OK", null)
										.show();
								}
							});

						}
						return;
					}

					String Token = Result.get("SESSION_ID").asText();
					TokenManager.set_token(Context, "", Token);

					//再起動
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Intent RestartIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
							RestartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(RestartIntent);
							Runtime.getRuntime().exit(0);
						}
					});
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}
		}).start();
	}
}
