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
	
	public float getMagSq() {
		return (x*x)+(y*y);
	}
	
	public String prettyString() {
		return String.format("(%s,%s)",x,y);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Vec2)) return false;
		Vec2 other = (Vec2)o;
		return x == other.getX() && y == other.getY();
	}
	
	public static Vec2 valueOf(String s) {
		String[] vals = s.split(COMMA);
		return new Vec2(Float.valueOf(vals[0]), Float.valueOf(vals[1]));
	}
	
	public static float[] toFloatArray(Vec2 vec) {
		float[] f = new float[2];
		f[0] = vec.getX();
		f[1] = vec.getY();
		return f;
	}
	
	public static Vec2 fromFloatArray(float[] f) {
		if(f == null || f.length < 2) throw new IllegalArgumentException("Could not load Vec2 from insufficient float array.");
		return new Vec2(f[0],f[1]);
	}

}
