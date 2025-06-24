package su.rumishistem.android.rumiserver.Module;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import su.rumishistem.android.rumiserver.Activity.LoginActivity;

public class TokenManager {
	public static final String PrefName = "token";

	public static void SetToken(LoginActivity Context, String UID, String Token) {
		try {
			//保存
			SharedPreferences PREF = Context.getSharedPreferences(PrefName, Context.MODE_PRIVATE);
			PREF.edit().putString("TOKEN", Token).commit();
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("登録中にエラー");
		}
	}

	public static boolean isLogin(SharedPreferences PREF) {
		String TokenBase64 = PREF.getString("TOKEN", null);
		return (TokenBase64 != null);
	}

	public static String getToken(SharedPreferences PREF) {
		try {
			return PREF.getString("TOKEN", null);
		} catch (Exception EX) {
			EX.printStackTrace();
			return null;
		}
	}
}
