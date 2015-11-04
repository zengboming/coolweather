package com.coolweather.app.util;

import android.util.Log;

public class LogUtil {

	public static final int VERBOSE=1;
	public static final int DEBUG=2;
	public static final int WARN=3;
	public static final int ERROR=4;
	public static final int NOTHING=6;
	public static final int ASSERT=5;
	public static final int LEVEL=ASSERT;
	
	public static void v(String tag,String msg){
		if(LEVEL<=VERBOSE){
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag,String msg){
		if(LEVEL<=DEBUG){
			Log.d(tag, msg);
		}
	}
	
	public static void w(String tag,String msg){
		if(LEVEL<=WARN){
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag,String msg){
		if(LEVEL<=ERROR){
			Log.e(tag, msg);
		}
	}
	
	public static void a(String tag,String msg){
		if(LEVEL<=ASSERT){
			Log.i(tag, msg);
		}
	}
	
}
