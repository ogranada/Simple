package com.framework.simple.tools;

import android.util.Log;

public class SimpleLog {

	private static String tag = "SimpleLog";

	public static void setTag(String tag) {
		SimpleLog.tag = tag;
	}

	public static void info(String msg) {
		Log.i(tag, msg);
	}

	public static void warning(String msg) {
		Log.w(tag, msg);
	}

	public static void debug(String msg) {
		Log.d(tag, msg);
	}

	public static void write(String msg) {
		debug(msg);
	}

	public static void error(Throwable e) {
		Log.e(tag, e.getMessage());
		for (StackTraceElement stte : e.getStackTrace()) {
			String val = "error at %s(%s)[%s]:%s";
			val = String.format(val, stte.getFileName(), stte.getClassName(),
					stte.getMethodName(), stte.getLineNumber());
			Log.e(tag, val);
		}
	}

}
