package com.solesurvivor.simplerender2_5.input;


import android.graphics.PointF;

public class CircleInputArea implements InputArea {
	
	private PointF mCenter;
	private PointF mModelCenter;
	private float mRadiusSq;
	private float mScale = 1.0f;
	
	public CircleInputArea(PointF center, float radius) {
		this.mModelCenter = center;
		this.mCenter = center;
		this.mRadiusSq = radius*radius;
	}

	@Override
	public boolean isPressed(PointF p) {
		float a = (p.x - mCenter.x);
		float b = (p.y - mCenter.y);
		return ((a*a) + (b*b)) < (mRadiusSq * mScale);
	}

	@Override
	public void scale(float x, float y, float z) {
		mScale = x;
	}

	@Override
	public void translate(float x, float y, float z) {
		PointF updCenter = new PointF(mCenter.x + x, mCenter.y + y);
		mCenter = updCenter;
	}

	@Override
	public void reset() {
		mCenter = mModelCenter;
		mScale = 1.0f;
	}

}
