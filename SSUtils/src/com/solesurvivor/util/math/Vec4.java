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
	
	public void normalize() {
		if(x != 0.0f || y != 0.0f || z != 0.0f) { //Cannot normalize 0 vector
			float len = (float)Math.sqrt((x*x) + (y*y) + (z*z));
			x /= len;
			y /= len;
			z /= len;
			w = 1/len;
		}
	}
	
	public Vec4 normalizeClone() {
		if(x != 0.0f || y != 0.0f || z != 0.0f) { //Cannot normalize 0 vector
			float len = (float)Math.sqrt((x*x) + (y*y) + (z*z));
			return new Vec4(x/len, y/len, z/len, 1/len);
		}
		
		return new Vec4(x,y,z,w);
	}
	
	public void add(Vec4 other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		this.w += other.w;
	}
	
	public void scale(float fac) {
		this.x *= fac;
		this.y *= fac;
		this.z *= fac;
		this.w *= fac;
	}
	
	public void componentScale(Vec4 other) {
		this.x *= other.x;
		this.y *= other.y;
		this.z *= other.z;
		this.w *= other.w;
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
	public String prettyString() {
		return String.format("Vec4(%s,%s,%s,%s)",x,y,z,w);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Vec4)) return false;
		Vec4 other = (Vec4)o;
		return super.equals(o) && w == other.getW();
	}
}
