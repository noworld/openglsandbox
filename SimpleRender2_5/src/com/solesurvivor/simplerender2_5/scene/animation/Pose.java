package com.solesurvivor.simplerender2_5.scene.animation;

public class Pose {

	protected String name;
	protected float[] bones;
	
	public Pose(String name, float[] bones) {
		super();
		this.name = name;
		this.bones = bones;
	}

	public String getName() {
		return name;
	}
	
	public float[] getBones() {
		return bones;
	}
	
}
