package com.solesurvivor.pthirtyeight.input;

import android.graphics.PointF;

public class ScreenInputArea implements InputArea {

	@Override
	public boolean isPressed(PointF p) {
		return true;
	}

	@Override
	public void scale(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PointF getInputCenter() {
		// TODO Auto-generated method stub
		return null;
	}

}
