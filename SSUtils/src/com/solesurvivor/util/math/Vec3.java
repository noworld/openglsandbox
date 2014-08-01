package com.solesurvivor.util.math;

public class Vec3 extends Vec2 {

	@SuppressWarnings("unused")
	private static final String TAG = Vec3.class.getSimpleName();

	float z = 1.0f;
	
	public Vec3() {
		
	}
	
	public Vec3(float x, float y, float z) {
		super(x,y);
		this.z = z;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	public static Vec3 valueOf(String s) {
		String[] vals = s.split(COMMA);
		return new Vec3(Float.valueOf(vals[0]), Float.valueOf(vals[1]), Float.valueOf(vals[2]));
	}

}
