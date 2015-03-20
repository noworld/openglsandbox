package com.solesurvivor.simplerender2_5.scene;

import com.solesurvivor.util.math.Vec3;

public class Actor extends GeometryNode {
	
	@SuppressWarnings("unused")
	private static final String TAG = Actor.class.getSimpleName();

	protected Vec3 location;
	
	public Actor(Geometry geometry) {
		super(geometry);
//		this.resetTransforms();
	}
	
	public Geometry getGeometry() {
		return mGeometry;
	}
	
}
