package com.pisen.ott.settings.common.location;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.pisen.ott.settings.util.KeyUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.izy.util.LogUtils;

/**
 * 城市数据库
 * @author Liuhc
 * @version 1.0 2014年12月3日 下午2:33:38
 */
public class CityDbHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.pisen.ott.settings/databases/";
	private static String DB_NAME = "citychina.db";
	private SQLiteDatabase db;
	private Context mContext;

	public CityDbHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.mContext = context;
		openDataBase();
	}
	
	public void openDataBase() {
		this.getReadableDatabase();
		db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
		
		//数据库不存在的话，拷贝
		if (!tabIsExist(db, CityInfo.Table.TABLE_PROVINCE)) {
			this.getReadableDatabase();
			copyDataBase();
		}
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() {
		try {
			InputStream myInput = mContext.getAssets().open(DB_NAME);
			String outFileName = DB_PATH + DB_NAME;
			OutputStream myOutput = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void close() {
		if (db != null)
			db.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	public boolean tabIsExist(SQLiteDatabase db,String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			LogUtils.e(KeyUtils.TAG_LHC, e.toString());
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return result;
	}
}
