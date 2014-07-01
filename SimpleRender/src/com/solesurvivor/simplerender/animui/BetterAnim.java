package com.solesurvivor.simplerender.animui;

import android.opengl.Matrix;

import com.solesurvivor.simplerender.Geometry;

/* New - for drawing multiple models*/
public class BetterAnim {	
	
	public static volatile int mScreenWidth = 1000;
	public static volatile int mScreenHeight = 1000;
	public static float mPadding = 10.0f;//px
	public static float mUiZ = -4.0f;	
	public static float mUiScale = 100.0f;
	public static float mSpriteScale = 0.6f;
	
	public static float[] initModelMatrix() {
		float[] newMatrix = new float[16]; 
		Matrix.setIdentityM(newMatrix, 0);
		return newMatrix;
	}
	
	public static Geometry updateSprite(Geometry geo) {
		
		Matrix.setIdentityM(geo.mModelMatrix, 0);		
		Matrix.translateM(geo.mModelMatrix, 0, 0.0f, 0.0f, -7.0f);
		Matrix.scaleM(geo.mModelMatrix, 0, mSpriteScale, mSpriteScale, 0.0f);
		
		return geo;
	}
	
	public static Geometry positionUI(Geometry ui) {
		Matrix.setIdentityM(ui.mModelMatrix, 0);
		float elWidth = ui.mXSize * mUiScale; //HACK! Bumping up 1BU = mUiScale px
		float elHeight = ui.mYSize * mUiScale;
		float elPriority = (((float)(ui.mPriority)) - 1.0f);
		
		switch(ui.mHAlign) {
		case LEFT: ui.mXOffset = (elWidth/2) + mPadding - (mScreenWidth/2) 
				+ (elPriority * elWidth) + (elPriority * mPadding);
			break;
		case CENTER: ui.mXOffset = 0.0f;
			break;		
		case RIGHT: ui.mXOffset = (mScreenWidth/2) - (elWidth/2) - mPadding 
				- (elPriority * elWidth) - (elPriority * mPadding);
			break;
		default:
			break;
		}
		
		switch(ui.mVAlign) {
		case BOTTOM: ui.mYOffset = (elHeight/2) + mPadding - (mScreenHeight/2);
			break;
		case MIDDLE: ui.mYOffset = 0.0f;
			break;
		case TOP: ui.mYOffset = (mScreenHeight/2) - (elHeight/2) - mPadding;
			break;
		default:
			break;
		
		}
		
		Matrix.translateM(ui.mModelMatrix, 0, ui.mXOffset, ui.mYOffset, mUiZ);
		Matrix.scaleM(ui.mModelMatrix, 0, mUiScale, mUiScale, 0.0f);		

		return ui;
	}
}
