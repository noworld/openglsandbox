package com.solesurvivor.simplescroller.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.solesurvivor.simplescroller.R;
import com.solesurvivor.util.SSPropertyUtil;

public class GameGlobal {

	public static final String SEPARATOR = "\\|";
	public static final String NEWLINE = "\r\n";
	public static final String PLUS = "+";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String ARRAY_RESOURCE_TYPE = "array";
	public static final String RESOURCE_PACKAGE = "com.solesurvivor.simplescroller";
	
	private static Map<GlobalKeysEnum,String> values;
	
	public static void init(Context c) {
		Map<GlobalKeysEnum,String> vals = readGlobalConfig(c);
		values = Collections.unmodifiableMap(vals);
	}
	
	public static String getVal(GlobalKeysEnum key) {
		return values.get(key);
	}
	
	public static Boolean getBool(GlobalKeysEnum key) {
		String boolVal = values.get(key);
		if(boolVal == null) return null;
		return Boolean.valueOf(boolVal);
	}
	
	private static Map<GlobalKeysEnum, String> readGlobalConfig(Context context) {
		String[] globals = context.getResources().getStringArray(R.array.global);
		
		Map<String,String> stringKeys = SSPropertyUtil.parseFromStringArray(globals,SEPARATOR);
		
		Map<GlobalKeysEnum,String> enumKeys = new HashMap<GlobalKeysEnum,String>(stringKeys.size());
		
		for(Map.Entry<String,String> entry: stringKeys.entrySet()) {
			enumKeys.put(GlobalKeysEnum.valueOf(entry.getKey()), entry.getValue());
		}
		
		return enumKeys;
	}
}
