package su.rumishistem.android.rumiserver.Activity.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import su.rumishistem.android.rumiserver.Activity.ProfileEditor;
import su.rumishistem.android.rumiserver.Module.API;
import su.rumishistem.android.rumiserver.R;

public class SettingFragment extends Fragment {
	private Context CTX;
	private HomeActivity Parent;

	public SettingFragment(HomeActivity ParentActivity) {
		this.CTX = this.getContext();
		this.Parent = ParentActivity;
	}

	@Override
	public View onCreateView(LayoutInflater Inflater, ViewGroup Container, Bundle SavedInstanceState) {
		return Inflater.inflate(R.layout.home_setting_fragment, Container, false);
	}

	@Override
	public void onViewCreated(View V, Bundle SavedInstanceState) {
		super.onViewCreated(V, SavedInstanceState);

		Button ProfileEditButton = V.findViewById(R.id.profile_edit);
		ProfileEditButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Parent.OpenActivity(ProfileEditor.class);
			}
		});
	}
}
