package com.solesurvivor.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class SSPropertyUtil {
	
//	public static final String LINE_SEP = System.getProperty("line.separator");
	public static final String LINE_SEP = "\r\n";
	public static final String EQUALS = "=";

	public static String parseFromMap(Map<String, String> mappy) {
		StringBuilder sb = new StringBuilder();

		for(Map.Entry<String,String> e : mappy.entrySet()) {
			if(sb.length() > 0) {
				sb.append(LINE_SEP);
			}
			sb.append(e.getKey()).append(EQUALS).append(e.getValue());
		}
		
		return sb.toString();
	}

	public static Map<String, String> parseFromString(String s) {
		Map<String, String> mappy = new HashMap<String, String>();
		
		String[] entries = s.split(LINE_SEP);
		
		for(int i = 0; i < entries.length; i++) {
			String[] kv = entries[i].split(EQUALS);
			if(kv.length > 1) {
				mappy.put(kv[0], kv[1]);
			} else if(kv.length == 1) {
				mappy.put(kv[0], StringUtils.EMPTY);
			}
		}
		
		return mappy;
	}
	
	public static Map<String, String> parseFromStringArray(String[] s, String separator) {
		Map<String,String> vals = new HashMap<String, String>();
		for(int i = 0; i < s.length; i++) {
			String[] keyVal = s[i].split(separator);
			vals.put(keyVal[0], keyVal[1]);
		}
		return vals;
	}
	
}
