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
	

	public void normalize() {
		if(x != 0.0f || y != 0.0f || z != 0.0f) { //Cannot normalize 0 vector
			float len = (float)Math.sqrt((x*x) + (y*y) + (z*z));
			x /= len;
			y /= len;
			z /= len;
		}
	}
	
	public Vec3 normalizeClone() {
		if(x != 0.0f || y != 0.0f || z != 0.0f) { //Cannot normalize 0 vector
			float len = (float)Math.sqrt((x*x) + (y*y) + (z*z));
			return new Vec3(x/len, y/len, z/len);
		}
		
		return new Vec3(x,y,z);
	}
	
	public Vec3 clone() {
		return new Vec3(x,y,z);
	}
	
	public void add(Vec3 other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
	}
	
	public void subtract(Vec3 other) {
		this.x -= other.x;
		this.y -= other.y;
		this.z -= other.z;
	}
	
	public void scale(float fac) {
		this.x *= fac;
		this.y *= fac;
		this.z *= fac;
	}
	
	public void componentScale(Vec3 other) {
		this.x *= other.x;
		this.y *= other.y;
		this.z *= other.z;
	}
	
	@Override
	public float getMag() {
		return (float)Math.sqrt(getMagSq());
	}

	public float getMagXZ() {
		return (float)Math.sqrt(getMagXZSq());
	}
	
	public float getMagXZSq() {
		return (x*x)+(z*z);
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
	
	public static float[] toFloatArray(Vec3 vec) {
		float[] f = new float[3];
		f[0] = vec.getX();
		f[1] = vec.getY();
		f[2] = vec.getZ();
		return f;
	}
	
	public static Vec3 fromFloatArray(float[] f) {
		if(f == null || f.length < 3) throw new IllegalArgumentException("Could not load Vec3 from insufficient float array.");
		return new Vec3(f[0],f[1],f[2]);
	}
	
	public static Vec3 valueOf(String s) {
		String[] vals = s.split(COMMA);
		return new Vec3(Float.valueOf(vals[0]), Float.valueOf(vals[1]), Float.valueOf(vals[2]));
	}
	
	public static Vec3 createZeroVec3() {
		return new Vec3(0.0f, 0.0f, 0.0f);
	}

}
