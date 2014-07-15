package com.pimphand.simplerender2.ui;

import android.graphics.PointF;

public interface InputArea {

	public boolean isPressed(PointF p);
	
	public void setScale(PointF factor);
}
