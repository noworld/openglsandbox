package com.solesurvivor.util.logging;

import android.util.Log;

/**
 * Generally, use the Log.v() Log.d() Log.i() Log.w() and Log.e() methods. 
 * @author nicholas.waun
 * (C)2014 Nicholas Waun. All rights reserved.
 */
public class SSLog {

	private static boolean sDebug = true;
	
	public static void setDebug(boolean debug) {
		sDebug = debug;
	}
	
	public static int d(String tag, String msg, Object... params) {
		return d(tag, String.format(msg, params));
	}
	
	public static int d(String tag, String msg) {
		return printlnDebug(Log.DEBUG, tag, msg);
	}

	public static int d(String tag, String msg, Throwable tr) {
		return printlnDebug(Log.DEBUG, tag, msg);
	}
	
	public static int e(String tag, String msg, Object... params) {
		return e(tag, String.format(msg, params));
	}
	
	public static int e(String tag, String msg) {
		return printlnDebug(Log.ERROR, tag, msg);
	}
	
	public static int e(String tag, String msg, Throwable tr) {
		return printlnDebug(Log.ERROR, tag, msg);
	}
	
	public static int i(String tag, String msg, Object... params) {
		return i(tag, String.format(msg, params));
	}
	
	public static int i(String tag, String msg) {
		return printlnDebug(Log.INFO, tag, msg);
	}
	
	public static int i(String tag, String msg, Throwable tr) {
		return printlnDebug(Log.INFO, tag, msg);
	}
	
	public static boolean isLoggable(String tag, int level) {
		return Log.isLoggable(tag, level);
	}
	
	protected static int printlnDebug(int priority, String tag, String msg) {
		return println(priority, tag, msg, sDebug);
	}
	
	protected static int println(int priority, String tag, String msg, boolean debug) {
		int ret = 0;
		
		if(debug) {
			switch(priority) {
			case Log.VERBOSE: 
				ret = Log.v(tag, msg);
				break;
			case Log.DEBUG:
				ret = Log.d(tag, msg);
				break;
			case Log.INFO:
				ret = Log.i(tag, msg);
				break;			
			}
		}
		
		switch(priority) {
		case Log.WARN:
			ret = Log.w(tag, msg);
			break;
		case Log.ERROR:
			ret = Log.e(tag, msg);
			break;
		}
		
		return ret;
	}
	
	public static int v(String tag, String msg, Object... params) {
		return v(tag, String.format(msg, params));
	}
	
	public static int v(String tag, String msg) {
		return printlnDebug(Log.VERBOSE, tag, msg);
	}
	
	public static int v(String tag, String msg, Throwable tr) {
		return printlnDebug(Log.VERBOSE, tag, msg);
	}
	
	public static int w(String tag, String msg, Object... params) {
		return w(tag, String.format(msg, params));
	}
	
	public static int d(String tag, String msg, Throwable tr, Object... params) {
		return d(tag, String.format(msg, params), tr);
	}
	
	public static int w(String tag, String msg, Throwable tr) {
		return printlnDebug(Log.WARN, tag, msg);
	}
	
	public static int w(String tag, String msg) {
		return printlnDebug(Log.WARN, tag, msg);
	}
	
	public static int wtf(String tag, Throwable tr) {
		return Log.wtf(tag, tr);
	}
	
	
	public static int wtf(String tag, String msg) {
		return Log.wtf(tag, msg);
	}
	
	public static int wtf(String tag, String msg, Throwable tr) {
		return Log.wtf(tag, msg, tr);
	}	

}
