package su.rumishistem.android.rumiserver.ForegroundService;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQL extends SQLiteOpenHelper {
	private static final String DB_NAME = "main.db";
	private static final int DB_VERSION = 3;

	public SQL(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//外部キー制約を有効化
		db.execSQL("PRAGMA foreign_keys = ON;");

		//テーブル作成
		db.execSQL("CREATE TABLE IF NOT EXISTS `ACCOUNT` (" +
			"ID TEXT PRIMARY KEY, " +
			"UID TEXT, " +
			"TOKEN TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
		db.execSQL("DROP TABLE IF EXISTS `ACCOUNT`;");

		onCreate(db);
	}
}
