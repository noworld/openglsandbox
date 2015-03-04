package com.solesurvivor.pthirtyeight.input;


import android.graphics.PointF;

public class CircleInputArea implements InputArea {
	
	private PointF center;
	private PointF modelCenter;
	private float radiusSq;
	private float scale = 1.0f;
	
	public CircleInputArea(PointF center, float radius) {
		this.modelCenter = center;
		this.center = center;
		this.radiusSq = radius*radius;
	}

	@Override
	public boolean isPressed(PointF p) {
		float a = (p.x - center.x);
		float b = (p.y - center.y);
		return ((a*a) + (b*b)) < (radiusSq * scale);
	}

	@Override
	public void scale(float x, float y, float z) {
		scale = x;
	}

	@Override
	public void translate(float x, float y, float z) {
		PointF updCenter = new PointF(center.x + x, center.y + y);
		center = updCenter;
	}

	@Override
	public void reset() {
		center = modelCenter;
		scale = 1.0f;
	}

	@Override
	public PointF getInputCenter() {
		// TODO Auto-generated method stub
		return null;
	}

}
