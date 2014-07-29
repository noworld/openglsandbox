package com.solesurvivor.simplerender2.game;

public class Physics {

	@SuppressWarnings("unused")
	private static final String TAG = Physics.class.getSimpleName();
	
	private static final Float GRAVITY = -1.0f;
	
	public static float applyGravity(float f, float deltaTSeconds) {
		return f + (GRAVITY * deltaTSeconds);
	}
}
