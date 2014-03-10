package com.framework.simple.datalayer.sources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.framework.simple.interfaces.Callback;
import com.framework.simple.interfaces.DataSource;

public class SQLiteDataSource extends SQLiteOpenHelper implements DataSource {

	public static int VERSION = 1;
	public static String NAME = "";
	public static Context CONTEXT = null;
	private static SQLiteDataSource instance = null;

	private static Map<String, List<Map<String, String>>> tables;

	private SQLiteDataSource(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	private SQLiteDataSource() {
		this(CONTEXT, NAME, null, VERSION);
	}

	public static SQLiteDataSource getSigletonInstance(Context context, String name,
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

	public static void addTable(String nombre, List<Map<String, String>> columns) {
		if (tables == null) {
			tables = new HashMap<String, List<Map<String,String>>>();
		}
		tables.put(nombre, columns);
	}

	public static Map<String, String> makeColumn(String name, String type,
			boolean pk, boolean autoincrement, String complement) {
		Map<String, String> col = new HashMap<String, String>();
		col.put("name", name);
		col.put("type", type);
		col.put("pk", pk ? "y" : "n");
		col.put("ai", pk ? "y" : "n");
		col.put("comp", complement);
		return col;
	}

	public static Map<String, String> makeColumn(String name, String type,
			boolean pk) {
		return makeColumn(name, type, pk, type.toUpperCase().equals("INTEGER")&&pk, "");
	}

	private String makeTableQuery(String tablename, List<Map<String,String>> columns) {
		String colname;
		String type;
		String primkey;
		String autoinc;
		String comp;
		String query = "CREATE TABLE IF NOT EXISTS %s(%s)";
		String cols = "";
		boolean first = true;
		for (Map<String, String> col : columns) {
			if(first){
				first = false;
			} else {
				cols += ", ";
			}
			colname = (String) col.get("name");
			type    = (String) col.get("type");
			primkey = ((String) col.get("pk")).equals("y")?"PRIMARY KEY":"";
			autoinc = ((String) col.get("ai")).equals("y")?"AUTOINCREMENT":"";
			comp    = (String) col.get("comp");
			cols += String.format("%s %s %s %s %s", colname, type, primkey, autoinc, comp).trim();
		}
		return String.format(query, tablename, cols);
	}
	
	public void createTables() {
		for (String name : tables.keySet()) {
			String query = makeTableQuery(name, tables.get(name));
			System.err.println(query);
			this.getWritableDatabase().execSQL(query);
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	public void getData(String apiSection, Map<String, String> params,
			Callback callback) {

	}

	@Override
	public void saveData(String apiSection, Map<String, String> params,
			Callback callback) {
	}

	@Override
	public void updateData(String apiSection, Map<String, String> params,
			Callback callback) {
	}

	@Override
	public void deleteData(String apiSection, String id, Callback callback) {
	}

}
