package com.solesurvivor.simplerender2.scene;


public class Light {

	public LightTypeEnum mLightType;
	
	public float mIntensity;
	public float[] mDirection = new float[3];
	public float[] mRGBColor = new float[3];
	
	public boolean mCastsShadow;
	
	public float[] mPosition = {0.0f,0.0f,0.0f,1.0f};
	public float[] mModelMatrix = new float[16];
	public int mShaderHandle;
		
}
