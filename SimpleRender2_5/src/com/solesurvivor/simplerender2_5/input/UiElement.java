package com.solesurvivor.simplerender2_5.input;

import com.solesurvivor.simplerender2_5.scene.Drawable;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.util.math.Vec3;

public class UiElement {

	protected Geometry mGeometry;

	public UiElement(Geometry geometry) {
		this.mGeometry = geometry;
	}

	public Drawable getGeometry() {
		return mGeometry;
	}

	public void scale(float x, float y, float z) {
		this.mGeometry.scale(new Vec3(x, y, z));
	}

	public void translate(float x, float y, float z) {
		this.mGeometry.translate(new Vec3(x, y, z));
	}
	
	public void rotate(float angle, float x, float y, float z) {
		this.mGeometry.rotate(angle, new Vec3(x, y, z));
	}

	public void reset() {
		this.mGeometry.resetTransforms();
	}
}
