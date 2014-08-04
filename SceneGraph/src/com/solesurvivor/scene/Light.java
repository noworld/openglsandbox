package com.solesurvivor.scene;

import com.solesurvivor.drawing.LightTypeEnum;

public class Light {

	public LightTypeEnum mLightType;
	
	public float mIntensity;
	public float[] mDirection = new float[3];
	public float[] mRGBColor = new float[3];
	
	public boolean mCastsShadow;
	
	public float[] mModelMatrix = new float[16];
	public float[] mPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public float[] mPosInWorldSpace = new float[4];
	public float[] mPosInEyeSpace = new float[4];
		
}
