package com.solesurvivor.util.math;

public class Vec2 {

	@SuppressWarnings("unused")
	private static final String TAG = Vec2.class.getSimpleName();
	
	protected static final String COMMA = ",";
	
	float x = 1.0f;
	float y = 1.0f;
	
	public Vec2() {
		
	}
	
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public static Vec2 valueOf(String s) {
		String[] vals = s.split(COMMA);
		return new Vec2(Float.valueOf(vals[0]), Float.valueOf(vals[1]));
	}

}
