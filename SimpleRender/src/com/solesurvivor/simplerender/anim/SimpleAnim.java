package com.solesurvivor.simplerender.anim;

import android.opengl.Matrix;

import com.solesurvivor.simplerender.Geometry;

/* New - for drawing multiple models*/
public class SimpleAnim {	
	
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
			Matrix.translateM(geo.mModelMatrix, 0, 0.0f, 0.0f, -4.0f);
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
