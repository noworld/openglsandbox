package com.solesurvivor.simplerender;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class InputHandler {

	public static volatile boolean mDUpPressed = false;
	public static volatile boolean mDDnPressed = false;
	public static volatile boolean mDLtPressed = false;
	public static volatile boolean mDRtPressed = false;
	public static volatile boolean mBtnAPressed = false;
	public static volatile boolean mBtnBPressed = false;
	
	public static List<InputArea> mInputs = new ArrayList<InputArea>();
	
	public static void reset() {
		mDUpPressed = false;
		mDDnPressed = false;
		mDLtPressed = false;
		mDRtPressed = false;
		
		mBtnAPressed = false;
		
		mBtnBPressed = false;
	}
	
	public static synchronized void touch(int x, int y) {
		Log.d("INPUT RECEIVED", String.format("Input received at location: %s, %s", x, y));
		
		for(InputArea ia : mInputs) {
			if(ia.mRadius > Float.MIN_VALUE) {
				//Circle
			} else {
				//Rectangle
			}
		}
		
	}
	
	
	public static class InputArea {
		public float mX;
		public float mY;
		public float mRadius = Float.MIN_VALUE; //For circle buttons
		public float mBLX; //For rectangle buttons
		public float mBLY; //For rectangle buttons
	}
	
}
