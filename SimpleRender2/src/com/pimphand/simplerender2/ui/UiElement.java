package com.pimphand.simplerender2.ui;

import android.opengl.Matrix;

import com.pimphand.simplerender2.rendering.Geometry;

public class UiElement {

	protected Geometry mGeometry;

	public UiElement(Geometry geometry) {
		this.mGeometry = geometry;
	}

	public Geometry getGeometry() {
		return mGeometry;
	}

	public void scale(float x, float y, float z) {
		Matrix.scaleM(this.mGeometry.mModelMatrix, 0, x, y, z);
	}

	public void translate(float x, float y, float z) {
		Matrix.translateM(this.mGeometry.mModelMatrix, 0, x, y, z);
	}

	public void reset() {
		Matrix.setIdentityM(this.mGeometry.mModelMatrix, 0);
	}
}
