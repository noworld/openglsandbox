package com.solesurvivor.simplerender2.commands;


public class RotateWaveRight extends RotateWaveLeft {

	@SuppressWarnings("unused")
	private static final String TAG = RotateWaveRight.class.getSimpleName();
	
	public RotateWaveRight() {
		this.mAngle = this.mAngle * -1;
		super.init();
	}

}
