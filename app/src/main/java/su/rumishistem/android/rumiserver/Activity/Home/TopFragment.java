package su.rumishistem.android.rumiserver.Activity.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import su.rumishistem.android.rumiserver.Module.UserIconManager;
import su.rumishistem.android.rumiserver.R;

public class TopFragment extends Fragment {
	private HomeActivity Parent;

	public TopFragment(HomeActivity ParentActivity) {
		this.Parent = ParentActivity;
	}

	@Override
	public View onCreateView(LayoutInflater Inflater, ViewGroup Container, Bundle SavedInstanceState) {
		return Inflater.inflate(R.layout.home_top_fragment, Container, false);
	}

	@Override
	public void onViewCreated(View V, Bundle SavedInstanceState) {
		super.onViewCreated(V, SavedInstanceState);

		ImageView user_icon_imageview = V.findViewById(R.id.user_icon_imageview);
		user_icon_imageview.setImageBitmap(UserIconManager.Get(Parent.getSelfUser().get("UID").asText()));

		TextView user_name_textview = V.findViewById(R.id.user_name_textview);
		user_name_textview.setText(Parent.getSelfUser().get("NAME").asText());
	}
}
