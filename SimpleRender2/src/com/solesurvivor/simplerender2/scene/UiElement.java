package com.solesurvivor.simplerender2.scene;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2.rendering.Geometry;
import com.solesurvivor.simplerender2.text.Cursor;

public class UiElement {

	protected Geometry mGeometry;
	protected Cursor mCursor;

	public UiElement(Geometry geometry) {
		this.mGeometry = geometry;
	}

	public Geometry getGeometry() {
		return mGeometry;
	}
	
	public void setCursor(Cursor cursor) {
		this.mCursor = cursor;
	}
	
	public Cursor getCursor() {
		return mCursor;
	}

	public void scale(float x, float y, float z) {
		Matrix.scaleM(this.mGeometry.mModelMatrix, 0, x, y, z);
		if(mCursor != null){mCursor.scale(x,y,z);}
	}

	public void translate(float x, float y, float z) {
		Matrix.translateM(this.mGeometry.mModelMatrix, 0, x, y, z);
		if(mCursor != null){mCursor.translate(x,y,z);}
	}

	public void reset() {
		Matrix.setIdentityM(this.mGeometry.mModelMatrix, 0);
		if(mCursor != null){mCursor.reset();}
	}
}
