package com.solesurvivor.simplerender2_5.commands;


public class WalkBackward extends WalkForward {

	@SuppressWarnings("unused")
	private static final String TAG = WalkBackward.class.getSimpleName();

	public WalkBackward() {
		this.mZDisp = -this.mZDisp;
	}
}
