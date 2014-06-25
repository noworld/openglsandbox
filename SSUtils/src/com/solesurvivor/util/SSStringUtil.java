package com.solesurvivor.util;

import org.apache.commons.lang.StringUtils;

public class SSStringUtil {

	public static String karateChar(String s, char c) {
		if(StringUtils.isBlank(s)) return null;
		
		String strVal = s;
		
		if(s.charAt(0) == c) {
			strVal = s.substring(1);
		}
		
		return strVal;
		
	}

}
