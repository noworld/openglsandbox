package com.solesurvivor.util.math;

import android.graphics.Point;

public class Vec2 {

	@SuppressWarnings("unused")
	private static final String TAG = Vec2.class.getSimpleName();
	
	protected static final String COMMA = ",";
	protected static final float ZERO_F = 0.0f;
	
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
	
	public float getMag() {
		return (float)Math.sqrt(getMagSq());
	}
	
	public float getMagSq() {
		return (x*x)+(y*y);
	}	
	
	public void subtract(Vec2 other) {
		this.x -= other.getX();
		this.y -= other.getY();
	}
	
	public String prettyString() {
		return String.format("Vec2(%s,%s)",x,y);
	}
	
	public void normalize() {
		if(x != ZERO_F || y != ZERO_F) { //Cannot normalize 0 vector
			float len = (float)Math.sqrt((x*x) + (y*y));
			x /= len;
			y /= len;
		}
	}
	
	public Vec2 normalizeClone() {
		if(x != ZERO_F || y != ZERO_F) { //Cannot normalize 0 vector
			float len = (float)Math.sqrt((x*x) + (y*y));
			return new Vec2(x/len, y/len);
		}
		
		return new Vec2(x,y);
	}
	
	public void add(Vec2 other) {
		this.x += other.x;
		this.y += other.y;
	}
	
	public void scale(float fac) {
		this.x *= fac;
		this.y *= fac;
	}
	
	public void componentScale(Vec2 other) {
		this.x *= other.x;
		this.y *= other.y;
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

	public static Vec2 createZeroVec2() {
		return new Vec2(0.0f,0.0f);
	}
	
	public static Vec2 fromPoint(Point p) {
		return new Vec2(p.x, p.y);
	}
}
