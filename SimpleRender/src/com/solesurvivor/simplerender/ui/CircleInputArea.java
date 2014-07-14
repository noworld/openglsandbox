package com.solesurvivor.simplerender.ui;

import android.graphics.PointF;

public class CircleInputArea implements InputArea {
	
	private PointF mCenter;
	private float mRadiusSq;
	
	public CircleInputArea(PointF center, float radius) {
		this.mCenter = center;
		this.mRadiusSq = radius*radius;
	}

	@Override
	public boolean isPressed(PointF p) {
		float a = (p.x - mCenter.x);
		float b = (p.y - mCenter.y);
		return ((a*a) + (b*b)) < mRadiusSq;
	}

}
