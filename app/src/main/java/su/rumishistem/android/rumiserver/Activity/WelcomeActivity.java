package su.rumishistem.android.rumiserver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import su.rumishistem.android.rumiserver.R;

public class WelcomeActivity extends AppCompatActivity {
	private WelcomeActivity Context;

	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		Context = this;

		//UI
		setContentView(R.layout.welcome_activity);

		Button LoginButton = findViewById(R.id.login_account);
		LoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View V) {
				Intent LoginIntent = new Intent(Context, LoginActivity.class);
				startActivity(LoginIntent);
			}
		});
	}
}
