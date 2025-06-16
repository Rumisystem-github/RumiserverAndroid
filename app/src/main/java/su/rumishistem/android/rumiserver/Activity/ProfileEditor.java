package su.rumishistem.android.rumiserver.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import su.rumishistem.android.rumiserver.Module.API;
import su.rumishistem.android.rumiserver.Module.UserIconManager;
import su.rumishistem.android.rumiserver.R;

public class ProfileEditor extends AppCompatActivity {
	private static final int REQUEST_CODE_PICK_FILE = 1001;

	private Context CTX;

	private byte[] IconData;

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

			//アイコン
			ImageView IconImageView = findViewById(R.id.user_icon_imageview);
			IconImageView.setImageBitmap(UserIconManager.Get(SelfUser.get("UID").asText()));
			IconImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent I = new Intent(Intent.ACTION_GET_CONTENT);
					I.setType("image/*");
					I.addCategory(Intent.CATEGORY_OPENABLE);
					startActivityForResult(Intent.createChooser(I, "アイコンを選択せよ"), REQUEST_CODE_PICK_FILE);
				}
			});

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
								//ユーザー情報
								HashMap<String, String> Body = new HashMap<>();
								if (!SelfUser.get("NAME").asText().equals(UserName)) {
									Body.put("NAME", UserName);
								}
								//変更する内容がないなら何もしない
								if (!Body.keySet().isEmpty()) {
									JsonNode Result = API.RunPatch("User", new ObjectMapper().writeValueAsString(Body), Token);
									if (!Result.get("STATUS").asBoolean()) {
										ShowToast("APIエラー");
										return;
									}
								}

								//アイコン変更
								if (IconData != null) {
									JsonNode Result = API.RunPostByte("Icon", IconData, Token);
									if (Result.get("STATUS").asBoolean()) {
										//アイコンのキャッシュをクリアして再表示
										UserIconManager.Clear(SelfUser.get("UID").asText());
										IconImageView.setImageBitmap(UserIconManager.Get(SelfUser.get("UID").asText()));
									} else {
										ShowToast("APIエラー:" + Result.get("ERR").asText());
										return;
									}
								}

								ShowToast("変更しました");
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

	@Override
	protected void onActivityResult(int RequestCode, int ResultCode, @Nullable Intent Data) {
		super.onActivityResult(RequestCode, ResultCode, Data);

		if (RequestCode == REQUEST_CODE_PICK_FILE & ResultCode == RESULT_OK) {
			if (Data == null) return;
			Uri FileURI = Data.getData();

			try {
				Bitmap OriginalBMP = MediaStore.Images.Media.getBitmap(getContentResolver(), FileURI);

				//256×256にリサイズ
				Bitmap ResizeBMP = Bitmap.createScaledBitmap(OriginalBMP, 256, 256, true);

				//PNGに変換
				ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
				ResizeBMP.compress(Bitmap.CompressFormat.PNG, 100, BAOS);
				IconData = BAOS.toByteArray();

				ShowToast("おけ");
			} catch (Exception EX) {
				EX.printStackTrace();
				ShowToast("画像の変換に失敗しました");
			}
		}
	}
}
