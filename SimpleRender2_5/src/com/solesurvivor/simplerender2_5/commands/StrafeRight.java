package com.solesurvivor.simplerender2_5.commands;


public class StrafeRight extends StrafeLeft {

	@SuppressWarnings("unused")
	private static final String TAG = StrafeRight.class.getSimpleName();
	
	public StrafeRight() {
		this.mXDisp = -this.mXDisp;
	}

}
