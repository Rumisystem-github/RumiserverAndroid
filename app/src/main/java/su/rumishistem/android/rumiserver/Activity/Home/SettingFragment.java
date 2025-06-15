package su.rumishistem.android.rumiserver.Activity.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import su.rumishistem.android.rumiserver.R;

public class SettingFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater Inflater, ViewGroup Container, Bundle SavedInstanceState) {
		return Inflater.inflate(R.layout.home_setting_fragment, Container, false);
	}
}
