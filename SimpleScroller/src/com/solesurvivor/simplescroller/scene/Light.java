package com.solesurvivor.simplescroller.scene;

import android.opengl.Matrix;


public class Light {

	public LightTypeEnum lightType;
	
	public float intensity;
	public float[] direction = new float[3];
	public float[] rgbaColor = {1.0f,1.0f,1.0f,1.0f};
	
	public boolean castsShadow;
	
	public float[] position = {0.0f,0.0f,0.0f,1.0f};
	public float[] modelMatrix = new float[16];
	public int shaderHandle;
	
	public Light() {
		Matrix.setIdentityM(modelMatrix, 0);
	}
		
}
