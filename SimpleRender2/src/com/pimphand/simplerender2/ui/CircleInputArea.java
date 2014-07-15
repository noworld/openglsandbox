package com.pimphand.simplerender2.ui;

import android.graphics.PointF;

public class CircleInputArea implements InputArea {
	
	private PointF mCenter;
	private float mRadiusSq;
	private float mScale = 1.0f;
	
	public CircleInputArea(PointF center, float radius) {
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
	public void setScale(PointF factor) {
		this.mScale = (factor.x + factor.y)/2;
	}

}
