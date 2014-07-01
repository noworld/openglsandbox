package com.solesurvivor.simplerender;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class InputHandler {
	
	public static final Map<String,InputArea> mInputs = Collections.synchronizedMap(new HashMap<String,InputArea>());
	
	public static synchronized void touch(int x, int y) {
		Log.d("INPUT RECEIVED", String.format("Input received at location: %s, %s", x, y));
		
		for(InputArea ia : mInputs.values()) {			
			if(ia.mRadius > Float.MIN_VALUE) {
				//Circle
				if(x >= (ia.mX - ia.mRadius)
					&& x <= (ia.mX + ia.mRadius)
					&& y >= (ia.mY - ia.mRadius)
					&& y <= (ia.mY + ia.mRadius))
				{
					ia.mPressed = true;
				}

			} else {
				if(x >= (ia.mX)
						&& x <= (ia.mBRX)
						&& y >= (ia.mY)
						&& y <= (ia.mBRY))
				{
					ia.mPressed = true;
				}
			}
		}
		
	}
	
	public static void reset() {
		for(InputArea ia : mInputs.values()) {
			ia.mPressed = false;
		}
	}
	
	
	public static class InputArea {
		public float mX; //Center for circles, top left for rectangles
		public float mY; //Center for circles, top left for rectangles
		public float mRadius = Float.MIN_VALUE; //For circle buttons
		public float mBRX; //Bottom right For rectangle buttons
		public float mBRY; //For rectangle buttons
		public boolean mPressed;
	}
	
}
