package com.framework.simple.datalayer.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

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

	public static SQLiteDataSource getSigletonInstance(Context context,
			String name, int version) {
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
			tables = new HashMap<String, List<Map<String, String>>>();
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
		return makeColumn(name, type, pk, type.toUpperCase().equals("INTEGER")
				&& pk, "");
	}

	private String makeTableQuery(String tablename,
			List<Map<String, String>> columns) {
		String colname;
		String type;
		String primkey;
		String autoinc;
		String comp;
		String query = "CREATE TABLE IF NOT EXISTS %s(%s)";
		String cols = "";
		boolean first = true;
		for (Map<String, String> col : columns) {
			if (first) {
				first = false;
			} else {
				cols += ", ";
			}
			colname = (String) col.get("name");
			type = (String) col.get("type");
			primkey = ((String) col.get("pk")).equals("y") ? "PRIMARY KEY" : "";
			autoinc = ((String) col.get("ai")).equals("y") ? "AUTOINCREMENT"
					: "";
			comp = (String) col.get("comp");
			cols += String.format("%s %s %s %s %s", colname, type, primkey,
					autoinc, comp).trim();
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

	public static Pair<String, String> Asign(String key, String value) {
		Pair<String, String> res = new Pair<String, String>(key, value);
		return res;
	}

	public static String Or(Pair<String, String> a, Pair<String, String> b) {
		String aa = String.format("%s='%s'", a.first,
				a.second.replaceAll("'", "''"));
		String bb = String.format("%s='%s'", b.first,
				b.second.replaceAll("'", "''"));
		return String.format("%s OR %s", aa, bb);
	}

	public static String And(Pair<String, String> a, Pair<String, String> b) {
		String aa = String.format("%s='%s'", a.first,
				a.second.replaceAll("'", "''"));
		String bb = String.format("%s='%s'", b.first,
				b.second.replaceAll("'", "''"));
		return String.format("%s AND %s", aa, bb);
	}

	@Override
	public void getData(String section, Map<String, String> params,
			Callback callback) {
		if (section.length()==0) {
			callback.onFinish(new ArrayList<Map<String,Object>>(0));
		}
		String query = "SELECT * FROM %s %s";
		String where = "";
		if (params != null && params.size() > 0) {
			where = "WHERE ";
			boolean first = true;
			for (String key : params.keySet()) {
				if (first) {
					first = false;
				} else {
					where += "AND ";
				}
				if (key.startsWith("$")) {
					where += String.format("%s='%s' ", key, params.get(key)
							.replaceAll("'", "''"));
				} else {
					where += String.format("%s ", params.get(key));
				}
			}
		}
		query = String.format(query, section, where).trim();
		List<Map<String, Object>> l = execute(query);
		if (callback != null) {
			callback.onFinish(l);
		}
	}

	@Override
	public void saveData(String section, Map<String, String> params,
			Callback callback) {
		if (params == null || params.size() == 0) {
			callback.onFinish(new ArrayList<Map<String, Object>>(0));
			return;
		}
		String query = "INSERT INTO %s(%s) VALUES(%s)";
		String cols = "", vals = "";
		if (params != null && params.size() > 0) {
			cols = "";
			vals = "";
			boolean first = true;
			for (String key : params.keySet()) {
				if (first) {
					first = false;
				} else {
					cols += ", ";
					vals += ", ";
				}
				cols += String.format("%s", key);
				vals += String.format("'%s'",
						params.get(key).replaceAll("'", "''"));
			}
		}
		query = String.format(query, section, cols, vals).trim();
		List<Map<String, Object>> l = execute(query);
		if (callback != null) {
			callback.onFinish(l);
		}
	}

	@Override
	public void updateData(String section, String pk,
			Map<String, String> params, Callback callback) {
		if (pk == null || pk.length() == 0 || params == null
				|| params.size() == 0) {
			callback.onFinish(new ArrayList<Map<String, Object>>(0));
			return;
		}
		String query = "UPDATE %s SET %s WHERE %s";
		if (!params.containsKey(pk)) {
			Log.e("SQLiteDataSource Error", String.format(
					"'%s' key does not exists into params map", pk));
			return;
		}
		String cols = "", vals = "";
		if (params != null && params.size() > 0) {
			vals = "";
			boolean first = true;
			for (String key : params.keySet()) {
				if (first) {
					first = false;
				} else {
					vals += ", ";
				}
				vals += String.format("%s='%s'", key, params.get(key)
						.replaceAll("'", "''"));
			}
		}
		query = String.format(query, section, cols, vals).trim();
		List<Map<String, Object>> l = execute(query);
		if (callback != null) {
			callback.onFinish(l);
		}
	}

	public List<Map<String, Object>> execute(String query) {
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>(0);
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		while (cursor.moveToNext()) {
			Map<String, Object> row = new HashMap<String, Object>();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				row.put(cursor.getColumnName(i), cursor.getString(i));
			}
			r.add(row);
		}
		return r;
	}

	@Override
	public void deleteData(String section, String id, Callback callback) {
		if (id == null || id.length() == 0) {
			callback.onFinish(new ArrayList<Map<String, Object>>(0));
			return;
		}
		String query = String.format("DELETE FROM %s WHERE id='%s'", section,
				id.replace("'", "''"));
		List<Map<String, Object>> l = execute(query);
		callback.onFinish(l);
	}

}
