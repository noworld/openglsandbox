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
	
	public float getA() {
		return w;
	}

	public static Vec4 valueOf(String s) {
		String[] vals = s.split(COMMA);
		return new Vec4(Float.valueOf(vals[0]), Float.valueOf(vals[1]),
				Float.valueOf(vals[2]), Float.valueOf(vals[3]));
	}

	public static float[] toFloatArray(Vec4 vec) {
		float[] f = new float[4];
		f[0] = vec.getX();
		f[1] = vec.getY();
		f[2] = vec.getZ();
		f[3] = vec.getW();
		return f;
	}
	
	public static Vec4 fromFloatArray(float[] f) {
		if(f == null || f.length < 4) throw new IllegalArgumentException("Could not load Vec4 from insufficient float array.");
		return new Vec4(f[0],f[1],f[2],f[3]);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Vec4)) return false;
		Vec4 other = (Vec4)o;
		return super.equals(o) && w == other.getW();
	}
}
