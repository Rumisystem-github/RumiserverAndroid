package su.rumishistem.android.rumiserver.Module;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import su.rumishistem.android.rumiserver.Activity.LoginActivity;
import su.rumishistem.android.rumiserver.ForegroundService.SQL;

public class TokenManager {
	public static void set_token(Context ctx, String uid, String token) {
		SQLiteDatabase db = new SQL(ctx).getWritableDatabase();
		db.execSQL("INSERT INTO `ACCOUNT` (`ID`, `UID`, `TOKEN`) VALUES (?, ?, ?);", new Object[]{
			UUID.randomUUID().toString(),
			uid,
			token
		});
	}

	public static boolean is_logined(Context ctx) {
		SQLiteDatabase db = new SQL(ctx).getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM `ACCOUNT`;", null);
		return c.moveToFirst();
	}

	public static String getToken(Context ctx) {
		SQLiteDatabase db = new SQL(ctx).getWritableDatabase();
		Cursor c = db.rawQuery("SELECT `TOKEN` FROM `ACCOUNT`;", null);
		if (c.moveToFirst()) {
			return c.getString(0);
		} else {
			return null;
		}
	}
}
