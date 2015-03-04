package com.solesurvivor.pthirtyeight.input;

import com.solesurvivor.pthirtyeight.scene.Geometry;
import com.solesurvivor.util.math.Vec3;

public class UiElement {

	protected Geometry geometry;

	public UiElement(Geometry geometry) {
		this.geometry = geometry;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void scale(float x, float y, float z) {
		this.geometry.scale(new Vec3(x, y, z));
	}

	public void translate(float x, float y, float z) {
		this.geometry.translate(new Vec3(x, y, z));
	}
	
	public void rotate(float angle, float x, float y, float z) {
		this.geometry.rotate(angle, new Vec3(x, y, z));
	}

	public void reset() {
		this.geometry.resetTransforms();
	}
}
