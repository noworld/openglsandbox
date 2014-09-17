package com.solesurvivor.simplerender2_5.input;

import android.database.Cursor;

import com.solesurvivor.simplerender2_5.scene.Drawable;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.util.math.Vec3;

public class UiElement {

	protected Drawable mGeometry;
//	protected Cursor mCursor;

	public UiElement(Drawable geometry) {
		this.mGeometry = geometry;
	}

	public Drawable getGeometry() {
		return mGeometry;
	}
	
	public void setCursor(Cursor cursor) {
//		this.mCursor = cursor;
	}
	
	public Cursor getCursor() {
//		return mCursor;
		return null;
	}

	public void scale(float x, float y, float z) {
//		this.mGeometry.scale(new Vec3(x, y, z));
//		if(mCursor != null){mCursor.scale(x,y,z);}
	}

	public void translate(float x, float y, float z) {
//		this.mGeometry.translate(new Vec3(x, y, z));
//		if(mCursor != null){mCursor.translate(x,y,z);}
	}

	public void reset() {
//		this.mGeometry.resetTransforms();
//		if(mCursor != null){mCursor.reset();}
	}
}
