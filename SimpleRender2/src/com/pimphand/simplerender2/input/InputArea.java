package com.pimphand.simplerender2.input;

import android.graphics.PointF;

public interface InputArea {

	public boolean isPressed(PointF p);

	public void scale(float x, float y, float z);
	
	public void translate(float x, float y, float z);
	
	public void reset();
}
