package com.solesurvivor.util.math;

public class Vec4 extends Vec3 {

	@SuppressWarnings("unused")
	private static final String TAG = Vec4.class.getSimpleName();

	float w = 1.0f;
	
	public Vec4() {
		
	}
	
	public Vec4(float x, float y, float z, float w) {
		super(x,y,z);
		this.w = w;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public static Vec4 valueOf(String s) {
		String[] vals = s.split(COMMA);
		return new Vec4(Float.valueOf(vals[0]), Float.valueOf(vals[1]),
				Float.valueOf(vals[2]), Float.valueOf(vals[3]));
	}
}
