package com.solesurvivor.simplerender2_5.commands;


public class FlyDown extends FlyUp {

	@SuppressWarnings("unused")
	private static final String TAG = FlyDown.class.getSimpleName();

	public FlyDown() {
		this.mYDisp = -this.mYDisp;
	}
}
