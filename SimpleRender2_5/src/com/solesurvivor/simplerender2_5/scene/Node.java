package com.solesurvivor.simplerender2_5.scene;

import com.solesurvivor.util.math.Vec3;

public interface Node {

	public void scale(Vec3 axes);
	public void rotate(float angle, Vec3 axes);
	public void translate(Vec3 trans);
	public void update();
	public void render();
	public void addChild(Node n);
	public boolean isDirty();
	public float[] getWorldMatrix();
	public float[] getTransMatrix();
	
}
