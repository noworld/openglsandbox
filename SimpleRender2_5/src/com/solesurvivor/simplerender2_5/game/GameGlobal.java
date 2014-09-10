package com.solesurvivor.simplerender2_5.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.WindowManager;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.util.SSPropertyUtil;
import com.solesurvivor.util.exceptions.NotInitializedException;

public class GameGlobal {

	public static final String SEPARATOR = "\\|";
	public static final String NEWLINE = "\r\n";
	public static final String PLUS = "+";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String ARRAY_RESOURCE_TYPE = "array";
	public static final String RESOURCE_PACKAGE = "com.solesurvivor.simplerender2_5";
	
	private static GameGlobal sInstance;
	
	private Context mContext;
	private WindowManager mWindowManager;
	private Map<GlobalKeysEnum,String> mValues;
	private Camera mCurrentCamera;
	
	private GameGlobal(Context context, WindowManager windowManager) {
		this.mContext = context;
		this.mWindowManager = windowManager;
		Map<GlobalKeysEnum,String> vals = readGlobalConfig();
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
		return mValues.get(key);
	}
	
	public Boolean getBool(GlobalKeysEnum key) {
		String boolVal = mValues.get(key);
		if(boolVal == null) return null;
		return Boolean.valueOf(boolVal);
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
	
	private Map<GlobalKeysEnum, String> readGlobalConfig() {
		String[] globals = mContext.getResources().getStringArray(R.array.global);
		
		Map<String,String> stringKeys = SSPropertyUtil.parseFromStringArray(globals,SEPARATOR);
		
		Map<GlobalKeysEnum,String> enumKeys = new HashMap<GlobalKeysEnum,String>(stringKeys.size());
		
		for(Map.Entry<String,String> entry: stringKeys.entrySet()) {
			enumKeys.put(GlobalKeysEnum.valueOf(entry.getKey()), entry.getValue());
		}
		
		return enumKeys;
	}
	
	public void setCamera(Camera cam) {
		this.mCurrentCamera = cam;
	}
	
	public Camera getCamera() {
		return this.mCurrentCamera;
	}
	
}
