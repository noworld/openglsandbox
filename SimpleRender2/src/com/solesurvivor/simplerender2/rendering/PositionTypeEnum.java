package com.solesurvivor.simplerender2.rendering;

public enum PositionTypeEnum {
	
	PERCENT("%"),
	PIXELS("px");
	
	private static final String ERR_MSG = "Suffix '%s' does not map to any known PositionTypeEnum.";
	private String mSuffix;
	
	private PositionTypeEnum(String suffix) {
		this.mSuffix = suffix;
	}
	
	public String getSuffix() {
		return mSuffix;
	}
	
	public static PositionTypeEnum fromSuffix(String suffix) {
		
		if(PERCENT.getSuffix().equals(suffix)) {
			return PERCENT;
		} else if(PIXELS.getSuffix().equals(suffix)) {
			return PIXELS;
		}
		
		throw new IllegalArgumentException(String.format(ERR_MSG, suffix));
	}
}
