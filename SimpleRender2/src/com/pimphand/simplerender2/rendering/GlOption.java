package com.pimphand.simplerender2.rendering;

public class GlOption {

	private int mIndex;
	private boolean mEnabled = true;
	
	public GlOption() {
		
	}
	
	public GlOption(int index) {
		this.mIndex = index;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	public void setIndex(int index) {
		this.mIndex = index;
	}
	
	public boolean isEnabled() {
		return mEnabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}
	
}
