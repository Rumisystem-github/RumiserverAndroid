package su.rumishistem.android.rumiserver.Module;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.SecureRandom;

public class AuthManager {
	private static final String PREFIX = "AUTH_DATA";

	private static SharedPreferences get_pref(Context ctx) {
		SharedPreferences pref = ctx.getSharedPreferences(PREFIX, Context.MODE_PRIVATE);
		return pref;
	}

	private static String gen_token() {
		int length = 100;
		String letter_list = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		StringBuilder sb = new StringBuilder();
		SecureRandom rnd = new SecureRandom();

		for (int i = 0; i < length; i++) {
			int index = rnd.nextInt(letter_list.length());
			sb.append(letter_list.charAt(index));
		}

		return sb.toString();
	}

	public static String regist(Context ctx, String package_name) {
		String token = gen_token();

		SharedPreferences.Editor editor = get_pref(ctx).edit();
		editor.putString(package_name.toUpperCase(), token);
		editor.commit();

		return token;
	}

	public static String get(Context ctx, String package_name) {
		String token = get_pref(ctx).getString(package_name.toUpperCase(), null);
		if (token == null) throw new RuntimeException("Token not found");

		return token;
	}
}
