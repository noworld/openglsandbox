package com.solesurvivor.scene;

public class Point extends GameNode {

	public float[] mModelMatrix = new float[16];
	public float[] mPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public float[] mPosInWorldSpace = new float[4];
	public float[] mPosInEyeSpace = new float[4];
	
}
