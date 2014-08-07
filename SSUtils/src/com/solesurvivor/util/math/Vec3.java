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
	
	public float getR() {
		return x;
	}
	
	public float getG() {
		return y;
	}
	
	public float getB() {
		return z;
	}
	
	@Override
	public float getMagSq() {
		return super.getMagSq()+(z*z);
	}
	
	@Override
	public String prettyString() {
		return String.format("(%s,%s,%s)",x,y,z);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Vec3)) return false;
		Vec3 other = (Vec3)o;
		return super.equals(o) && z == other.getZ();
	}
	
	public static Vec3 valueOf(String s) {
		String[] vals = s.split(COMMA);
		return new Vec3(Float.valueOf(vals[0]), Float.valueOf(vals[1]), Float.valueOf(vals[2]));
	}

}
