package com.pimphand.simplerender2.game;

import java.util.Collections;
import java.util.Map;

import android.content.Context;

import com.pimphand.simplerender2.R;
import com.solesurvivor.util.SSPropertyUtil;
import com.solesurvivor.util.exceptions.NotInitializedException;

public class GameGlobal {

	public static final String SEPARATOR = "|";
	
	private static GameGlobal sInstance;
	
	private Context mContext;
	
	private Map<String,String> mValues;
	
	private GameGlobal(Context context) {
		this.mContext = context;
		Map<String,String> vals = readGlobalConfig();
		mValues = Collections.unmodifiableMap(vals);
	}
	
	public static void init(Context context) {
		sInstance = new GameGlobal(context);
	}
	
	public static GameGlobal instance() {
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
	
	private Map<String, String> readGlobalConfig() {
		String[] globals = mContext.getResources().getStringArray(R.array.global);
		
		Map<String,String> vals = SSPropertyUtil.parseFromStringArray(globals,SEPARATOR);
		
		return vals;
	}
	
}
