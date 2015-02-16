package com.solesurvivor.simplerender2_5.scene;

import java.util.List;

public interface Drawable {

	public int getShaderHandle();
	public int getTextureHandle();
	public int getDatBufHandle();
	public int getIdxBufHandle();
	public int getPosSize();
	public int getNrmSize();
	public int getTxcSize();
	public int getNumElements();
	public int getElementStride();
	public int getElementOffset();
	public int getPosOffset();
	public int getNrmOffset();
	public int getTxcOffset();
	public float[] getWorldMatrix();
	public List<Light> getLights();

}
