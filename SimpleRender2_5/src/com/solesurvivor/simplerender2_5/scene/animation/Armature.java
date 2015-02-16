package com.solesurvivor.simplerender2_5.scene.animation;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.scene.NodeImpl;

public class Armature extends NodeImpl {

	protected String name;
	protected Bone[] bones;
	
	public Armature(String name, Bone[] bones) {
		super();
		this.name = name;
		this.bones = bones;
		updateBones();
	}

	public String getName() {
		return name;
	}
	
	public Bone[] getBones() {
		return bones;
	}
	
	protected void updateBones() {
		float[] ident = new float[16];
		Matrix.setIdentityM(ident, 0);
		for(Bone b : bones) {
			if(b.isRoot()){b.calculateCombinedMatrix(ident);};
		}
	}
}
