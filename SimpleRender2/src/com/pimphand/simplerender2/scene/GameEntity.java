package com.pimphand.simplerender2.scene;

import com.pimphand.simplerender2.rendering.Geometry;


public class GameEntity extends GameObject {

	private Geometry mGeometry;
	
	public GameEntity(Geometry geometry) {
		this.mGeometry = geometry;
	}
	
	public Geometry getGeometry() {
		return mGeometry;
	}
}
