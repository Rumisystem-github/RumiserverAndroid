package su.rumishistem.android.rumiserver.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import su.rumishistem.android.rumiserver.Module.API;
import su.rumishistem.android.rumiserver.R;

public class ProfileEditor extends AppCompatActivity {
	private Context CTX;

	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		try {
			super.onCreate(SavedInstanceState);

			this.CTX = this;

			Intent I = getIntent();
			String Token = I.getStringExtra("TOKEN");
			JsonNode SelfUser = new ObjectMapper().readTree(I.getStringExtra("SELF"));

			//UI
			setContentView(R.layout.profile_editor);

			//ユーザー名
			TextInputEditText UserNameInput = findViewById(R.id.username_input);
			UserNameInput.setText(SelfUser.get("NAME").asText());

			//適用ボタン
			Button ApplyButton = findViewById(R.id.apply_btn);
			ApplyButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String UserName = UserNameInput.getText().toString();
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								//ポストする内容
								HashMap<String, String> Body = new HashMap<>();
								Body.put("NAME", UserName);

								//GOGOGO
								JsonNode Result = API.RunPatch("User", new ObjectMapper().writeValueAsString(Body), Token);
								if (Result.get("STATUS").asBoolean()) {
									ShowToast("変更しました");
								} else {
									ShowToast("APIエラー");
								}
							} catch (Exception EX) {
								EX.printStackTrace();
							}
						}
					}).start();
				}
			});
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	public void ShowToast(String Text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(CTX, Text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}
}
