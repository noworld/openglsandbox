package com.solesurvivor.simplerender2_5.scene.animation;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.util.math.Vec3;


public class Bone {

	protected String name;
	protected int index;
	protected Bone parent;
	protected Vec3 head;
	protected Vec3 tail;
	protected float[] matrix = null;
	protected float[] bindMatrix = null; //XXX HACK this needs to be a separate armature
	protected float[] invBindMatrix = null; //XXX HACK this needs to be a separate armature
	protected float[] combinedBindMatrix = null;
	protected float[] invCombinedBindMatrix = null;
	protected List<Bone> children;
	protected boolean root = false;
	
	public Bone(String name) {
		children = new ArrayList<Bone>();
		this.name = name;
	}
	
	public Bone(String name, Boolean root) {
		this(name);
		this.root = root;
	}
	
	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setParent(Bone parent) {
		this.parent = parent;
	}
	
	public Bone getParent() {
		return parent;
	}
	
	public void setMatrix(float[] matrix) {
		this.matrix = matrix;
	}
	
	public float[] getMatrix() {
		return matrix;
	}
	
	public float[] getBindMatrix() {
		return bindMatrix;
	}

	public void setBindMatrix(float[] bindMatrix) {
		this.bindMatrix = bindMatrix;
	}

	public float[] getInvBindMatrix() {
		return invBindMatrix;
	}

	public void setInvBindMatrix(float[] invBindMatrix) {
		this.invBindMatrix = invBindMatrix;
	}

	public float[] getCombinedBindMatrix() {
		return combinedBindMatrix;
	}

	public void setCombinedBindMatrix(float[] combinedBindMatrix) {
		this.combinedBindMatrix = combinedBindMatrix;
	}

	public float[] getInvCombinedBindMatrix() {
		return invCombinedBindMatrix;
	}

	public void setInvCombinedBindMatrix(float[] invCombinedBindMatrix) {
		this.invCombinedBindMatrix = invCombinedBindMatrix;
	}

	public Vec3 getHead() {
		return head;
	}

	public void setHead(Vec3 head) {
		this.head = head;
	}

	public Vec3 getTail() {
		return tail;
	}

	public void setTail(Vec3 tail) {
		this.tail = tail;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public void addChild(Bone bone) {
		this.children.add(bone);
	}
	
	public void calculateCombinedMatrix(float[] parentCombinedBindMatrix) {
		
		this.combinedBindMatrix = new float[16];
		Matrix.multiplyMM(this.combinedBindMatrix, 0, this.bindMatrix, 0, parentCombinedBindMatrix, 0);
		this.invCombinedBindMatrix = new float[16];
		Matrix.invertM(this.invCombinedBindMatrix , 0, this.combinedBindMatrix, 0);
		
		for(Bone b : children) {			
			b.calculateCombinedMatrix(this.combinedBindMatrix);
		}
	}
}
