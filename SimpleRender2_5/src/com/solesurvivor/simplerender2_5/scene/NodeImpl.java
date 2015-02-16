package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.util.math.Vec3;

public class NodeImpl implements Node {

	protected List<Node> children;
	protected Vec3 position;
	protected float[] mTransMatrix;
	protected float[] mRotMatrix;
	protected float[] mScaleMatrix;
	protected float[] mWorldMatrix;
	protected float[] mTempMatrix;
	protected boolean mDirty = true;
	protected Node parent;

	public NodeImpl() {		
		position = Vec3.createZeroVec3();
		children = new ArrayList<Node>();
		mTransMatrix = new float[16];
		mRotMatrix = new float[16];
		mScaleMatrix = new float[16];
		mWorldMatrix = new float[16];
		mTempMatrix = new float[16];
		Matrix.setIdentityM(mTransMatrix, 0);
		Matrix.setIdentityM(mRotMatrix, 0);
		Matrix.setIdentityM(mScaleMatrix, 0);
		Matrix.setIdentityM(mWorldMatrix, 0);
		Matrix.setIdentityM(mTempMatrix, 0);
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

		if(mDirty || (parent != null && parent.isDirty())) {
			recalcMatrix();
			this.mDirty = true;
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
		Matrix.scaleM(mScaleMatrix, 0, axes.getX(), axes.getY(), axes.getZ());
		mDirty = true;
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		Matrix.rotateM(mRotMatrix, 0, angle, axes.getX(), axes.getY(), axes.getZ());
		mDirty = true;
	}

	@Override
	public void translate(Vec3 trans) {
		position.add(trans);
		Matrix.translateM(mTransMatrix, 0, trans.getX(), trans.getY(), trans.getZ());
		mDirty = true;
	}

	@Override
	public float[] getWorldMatrix() {

		if(mDirty) {
			recalcMatrix();
//			mDirty = false;
		}

		return mWorldMatrix;
	}
	
	@Override
	public float[] getTransMatrix() {
		
		if(mDirty) {
			recalcMatrix();
//			mDirty = false;
		}
		
		return mTransMatrix;
	}

	@Override
	public boolean isDirty() {
		return mDirty;
	}
	
	protected void renderChildren() {
		for(Node n : children) {
			n.render();
		}
	}

	protected void recalcMatrix() {

		Matrix.setIdentityM(mWorldMatrix, 0);
		Matrix.setIdentityM(mTempMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mWorldMatrix, 0, mScaleMatrix, 0);		
		Matrix.multiplyMM(mWorldMatrix, 0, mTempMatrix, 0, mTransMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mWorldMatrix, 0, mRotMatrix, 0);

		if(parent != null) {
			Matrix.multiplyMM(mWorldMatrix, 0, parent.getWorldMatrix(), 0, mTempMatrix, 0);
		} else {
			System.arraycopy(mTempMatrix, 0, mWorldMatrix, 0, mTempMatrix.length);
		}
	}
}
