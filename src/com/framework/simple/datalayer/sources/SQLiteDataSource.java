package com.framework.simple.datalayer.sources;

import java.util.Map;

import android.R.string;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.framework.simple.interfaces.Callback;
import com.framework.simple.interfaces.DataSource;

public class SQLiteDataSource extends SQLiteOpenHelper implements DataSource {

	public static int VERSION = 1;
	public static String NAME = "";
	public static Context CONTEXT = null;
	private static SQLiteDataSource instance = null;

	private SQLiteDataSource(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	private SQLiteDataSource() {
		this(CONTEXT, NAME, null, VERSION);
	}

	public SQLiteDataSource getSigletonInstance(Context context, String name,
			int version) {
		CONTEXT = context;
		NAME = name;
		VERSION = version;
		if (instance == null) {
			instance = new SQLiteDataSource(CONTEXT, NAME, null, VERSION);
		}
		return instance;
	}

	public SQLiteDataSource getNewInstance(Context context, String name,
			int version) {
		SQLiteDataSource instance = null;
		if (instance == null) {
			instance = new SQLiteDataSource(context, name, null, version);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO: add tables dynamically
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getData(String apiSection, Map<String, String> params,
			Callback callback) {

	}

	@Override
	public void postData(String apiSection, Map<String, String> params,
			Callback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void putData(String apiSection, Map<String, String> params,
			Callback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteData(String apiSection, String id, Callback callback) {
		// TODO Auto-generated method stub

	}

}
