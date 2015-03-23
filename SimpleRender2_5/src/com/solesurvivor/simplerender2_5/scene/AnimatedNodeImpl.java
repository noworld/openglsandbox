package com.solesurvivor.simplerender2_5.scene;

import android.opengl.Matrix;

import com.solesurvivor.util.math.Vec3;

public class AnimatedNodeImpl extends GeometryNode {

	protected float[] animMatrix;

	public AnimatedNodeImpl(Geometry g) {
		super(g);
		this.animMatrix = new float[16];
		Matrix.setIdentityM(animMatrix, 0);
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
	
	public void resetAnimation() {
		Matrix.setIdentityM(animMatrix, 0);
	}
	
	public void translateAnimation(Vec3 trans) {
		Matrix.translateM(animMatrix, 0, trans.getX(), trans.getY(), trans.getZ());
		mDirty = true;
	}
	
	public void rotateAnimation(float angle, Vec3 axes) {
		Matrix.rotateM(animMatrix, 0, angle, axes.getX(), axes.getY(), axes.getZ());
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
		System.arraycopy(mWorldMatrix, 0, mTempMatrix, 0, mTempMatrix.length);
		Matrix.multiplyMM(mWorldMatrix, 0, mTempMatrix, 0, animMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mWorldMatrix, 0, mRotMatrix, 0);

		if(parent != null) {
			Matrix.multiplyMM(mWorldMatrix, 0, parent.getWorldMatrix(), 0, mTempMatrix, 0);
		} else {
			System.arraycopy(mTempMatrix, 0, mWorldMatrix, 0, mTempMatrix.length);
		}
		
	}

	@Override
	public void resetTranslation() {
		// TODO Auto-generated method stub
		
	}
}
