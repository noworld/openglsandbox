package com.solesurvivor.simplerender.anim;

import android.opengl.Matrix;

import com.solesurvivor.simplerender.Geometry;

/* New - for drawing multiple models*/
public class SimpleAnim {	
	
	public static volatile int mScreenWidth = 1000;
	public static volatile int mScreenHeight = 1000;
	
	public static float[] initModelMatrix() {
		float[] newMatrix = new float[16]; 
		Matrix.setIdentityM(newMatrix, 0);
		return newMatrix;
	}
	
	public static Geometry update(Geometry geo) {
		
		Matrix.setIdentityM(geo.mModelMatrix, 0);
		
		if(geo.mName.equals("Suzanne-mesh")) {			
			if(geo.mAccumScale < 2.0f) {
				geo.mAccumScale += 0.005f;
			}
			geo.mAccumRot += 0.5f;		
			Matrix.translateM(geo.mModelMatrix, 0, 0.0f, 0.0f, -7.0f);
			Matrix.scaleM(geo.mModelMatrix, 0, geo.mAccumScale, geo.mAccumScale, geo.mAccumScale);
			Matrix.rotateM(geo.mModelMatrix, 0, geo.mAccumRot, 0.5f, 0.5f, 0.0f);	
		} else if(geo.mName.equals("foreplane-mesh")) {
			Matrix.translateM(geo.mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
		} else if(geo.mName.equals("backplane-mesh")) {
			Matrix.translateM(geo.mModelMatrix, 0, 0.0f, 0.0f, -8.0f);
		} else if(geo.mName.equals("gobutton-mesh")) {			
			//From the model, this button is 4:2 BU, which we will scale up by 100 since we are
			//operating in pixels with our ortho camera, which gives us a 400x200 button.
			//So move it so it is to the bottom left
			int buttonWidth = 400;
			int buttonHeight = 200;
			float padding = 10.0f;//px
			float yOffset = (buttonWidth/2) + padding - (mScreenWidth/2);
			float xOffset = (buttonHeight/2) + padding - (mScreenHeight/2);
			Matrix.translateM(geo.mModelMatrix, 0, yOffset, xOffset, -4.0f);
			//Rendering Ortho so scale is % of original size
			Matrix.scaleM(geo.mModelMatrix, 0, 100.0f, 100.0f, 0.0f);											
		} else {
			if(geo.mAccumScale > 0.01f) {
				geo.mAccumScale -= 0.005f;
			}
			geo.mAccumRot += 0.3f;
			Matrix.translateM(geo.mModelMatrix, 0, 0.0f, 0.0f, -7.0f);
			Matrix.scaleM(geo.mModelMatrix, 0, geo.mAccumScale, geo.mAccumScale, geo.mAccumScale);
			Matrix.rotateM(geo.mModelMatrix, 0, geo.mAccumRot, 0.5f, 0.5f, 0.0f);	
		}
				
		
			
		
		return geo;
	}
}
