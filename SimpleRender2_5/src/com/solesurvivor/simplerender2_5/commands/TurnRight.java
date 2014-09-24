package com.solesurvivor.simplerender2_5.commands;


public class TurnRight extends TurnLeft {

	@SuppressWarnings("unused")
	private static final String TAG = TurnRight.class.getSimpleName();

	public TurnRight() {
		this.mYRot = -this.mYRot;
	}
}
