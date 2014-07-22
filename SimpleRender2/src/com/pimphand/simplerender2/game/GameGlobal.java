package com.pimphand.simplerender2.game;

import java.util.Collections;
import java.util.Map;

import android.content.Context;
import android.view.WindowManager;

import com.pimphand.simplerender2.R;
import com.solesurvivor.util.SSPropertyUtil;
import com.solesurvivor.util.exceptions.NotInitializedException;

public class GameGlobal {

	public static final String SEPARATOR = "\\|";
	
	private static GameGlobal sInstance;
	
	private Context mContext;
	
	private WindowManager mWindowManager;
	
	private Map<String,String> mValues;
	
	private GameGlobal(Context context, WindowManager windowManager) {
		this.mContext = context;
		this.mWindowManager = windowManager;
		Map<String,String> vals = readGlobalConfig();
		mValues = Collections.unmodifiableMap(vals);
	}
	
	public static void init(Context context, WindowManager windowManager) {
		sInstance = new GameGlobal(context, windowManager);
	}
	
	public static GameGlobal inst() {
		if(sInstance == null) {
			throw new NotInitializedException();
		}
		return sInstance;
	}
	
	public String getVal(GlobalKeysEnum key) {
		return mValues.get(key.toString());
	}
	
	public Context getContext() {
		if(mContext == null) {
			throw new NotInitializedException();
		}
		return mContext;
	}
	
	public WindowManager getWindowManager() {
		if(mWindowManager == null) {
			throw new NotInitializedException();
		}
		
		return mWindowManager;
	}
	
	private Map<String, String> readGlobalConfig() {
		String[] globals = mContext.getResources().getStringArray(R.array.global);
		
		Map<String,String> vals = SSPropertyUtil.parseFromStringArray(globals,SEPARATOR);
		
		return vals;
	}
	
}
