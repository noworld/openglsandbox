package com.solesurvivor.simplescroller.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.util.math.Vec3;

public class NodeImpl implements Node {

	protected List<Node> children;
	protected Vec3 position;
	protected float[] transMatrix;
	protected float[] rotMatrix;
	protected float[] scaleMatrix;
	protected float[] worldMatrix;
	protected float[] tempMatrix;
	protected boolean dirty = true;
	protected Node parent;

	public NodeImpl() {		
		position = Vec3.createZeroVec3();
		children = new ArrayList<Node>();
		transMatrix = new float[16];
		rotMatrix = new float[16];
		scaleMatrix = new float[16];
		worldMatrix = new float[16];
		tempMatrix = new float[16];
		Matrix.setIdentityM(transMatrix, 0);
		Matrix.setIdentityM(rotMatrix, 0);
		Matrix.setIdentityM(scaleMatrix, 0);
		Matrix.setIdentityM(worldMatrix, 0);
		Matrix.setIdentityM(tempMatrix, 0);
	}

	public NodeImpl(Node parent) {
		this();
		this.parent = parent;
	}
	
	public Vec3 getPosition() {
		return position;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}

	@Override
	public void update() {
		
		//XXX should I have another method/s to override
		//to make this code more reusable?
		//e.g. preUpdate();

		if(dirty || (parent != null && parent.isDirty())) {
			recalcMatrix();
			this.dirty = true;
		}

		for(Node n : children) {
			n.update();
		}
		
		//e.g. postUpdate();
		
//		this.mDirty = false;
	}

	@Override
	public void render() {
		renderChildren();
	}

	//XXX Change to also link up the parent
	@Override
	public void addChild(Node n) {
		children.add(n);
	}

	@Override
	public void scale(Vec3 axes) {
		Matrix.scaleM(scaleMatrix, 0, axes.getX(), axes.getY(), axes.getZ());
		dirty = true;
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		Matrix.rotateM(rotMatrix, 0, angle, axes.getX(), axes.getY(), axes.getZ());
		dirty = true;
	}

	@Override
	public void translate(Vec3 trans) {
		position.add(trans);
		Matrix.translateM(transMatrix, 0, trans.getX(), trans.getY(), trans.getZ());
		dirty = true;
	}

	@Override
	public float[] getWorldMatrix() {

		if(dirty) {
			recalcMatrix();
//			mDirty = false;
		}

		return worldMatrix;
	}
	
	@Override
	public float[] getTransMatrix() {
		
		if(dirty) {
			recalcMatrix();
//			mDirty = false;
		}
		
		return transMatrix;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	protected void renderChildren() {
		for(Node n : children) {
			n.render();
		}
	}

	protected void recalcMatrix() {

		Matrix.setIdentityM(worldMatrix, 0);
		Matrix.setIdentityM(tempMatrix, 0);
		Matrix.multiplyMM(tempMatrix, 0, worldMatrix, 0, scaleMatrix, 0);		
		Matrix.multiplyMM(worldMatrix, 0, tempMatrix, 0, transMatrix, 0);
		Matrix.multiplyMM(tempMatrix, 0, worldMatrix, 0, rotMatrix, 0);

		if(parent != null) {
			Matrix.multiplyMM(worldMatrix, 0, parent.getWorldMatrix(), 0, tempMatrix, 0);
		} else {
			System.arraycopy(tempMatrix, 0, worldMatrix, 0, tempMatrix.length);
		}
	}
}
