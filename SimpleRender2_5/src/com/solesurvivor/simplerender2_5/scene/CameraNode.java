package com.solesurvivor.simplerender2_5.scene;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;


public class CameraNode extends StatefulNodeImpl {

	private static final String TAG = CameraNode.class.getSimpleName();

	protected float[] baseMatrix;
	protected float[] viewMatrix;
	protected float[] reflectMatrix;
	protected float[] projMatrix;
	protected float[] orthoMatrix;
	protected Vec3 eyePos;
	protected Point viewport = new Point(0,0);
	protected float near = 1.0f;
	protected float far = 1000.0f; //200.0f;
	protected float fov = 50.0f;
	
	public CameraNode(Vec3 eyePos, Vec3 look, Vec3 up, float near, float far, float fov, Node parent) {
		this(eyePos, look, up, near, far, fov);
		this.parent = parent;
	}

	public CameraNode(Vec3 eyePos, Vec3 look, Vec3 up, float near, float far, float fov) {
		this(eyePos, look, up);
		this.near = near;
		this.far = far;
		this.fov = fov;
	}
	
	public CameraNode(Vec3 eyePos, Vec3 look, Vec3 up, Node parent) {
		this(eyePos, look, up);
		this.parent = parent;
	}

	public CameraNode(Vec3 eyePos, Vec3 look, Vec3 up) {
		viewMatrix = new float[16];
		reflectMatrix = new float[16];
		orthoMatrix = new float[16];
		projMatrix = new float[16];
		baseMatrix = new float[16];
		this.eyePos = eyePos;
		Matrix.setLookAtM(baseMatrix, 0, eyePos.getX(), eyePos.getY(), eyePos.getZ(), 
				look.getX(), look.getY(), look.getZ(),
				up.getX(), up.getY(), up.getZ());
		recalcMatrix();
	}
	
	public Vec3 getEyePos() {
		return this.eyePos;
	}

	public float[] getViewMatrix() {
		return viewMatrix;
	}

	public float[] getOrthoMatrix() {
		return orthoMatrix;
	}

	public float[] getProjectionMatrix() {
		return projMatrix;
	}

//	@Override
//	public void update() {
//
//		if(parent != null && parent.isDirty()) {
//			this.recalcMatrix();
//			this.mDirty = true;
//		}
//
//		for(Node n : children) {
//			n.update();
//		}
//
//		this.mDirty = false;
//	}

	public void resizeViewport(Point newViewport) {
		if(newViewport == null) {
			SSLog.d(TAG, "Null viewport sent to camera.");
			return;
		} else {
			SSLog.d(TAG, "Resizing camera viewport: %s,%s", newViewport.x, newViewport.y);
		}

		this.viewport = newViewport;

		recalcProjMatrix();
	}
	
	@Override
	protected void recalcMatrix() {

		Matrix.setIdentityM(mWorldMatrix, 0);
		Matrix.setIdentityM(mTempMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mWorldMatrix, 0, mScaleMatrix, 0);
		Matrix.multiplyMM(mWorldMatrix, 0, mTempMatrix, 0, mRotMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mWorldMatrix, 0, mTransMatrix, 0);

		if(parent != null) {
			//Only want to follow translation
			Matrix.multiplyMM(mWorldMatrix, 0, parent.getTransMatrix(), 0, mTempMatrix, 0);
		} else {
			System.arraycopy(mTempMatrix, 0, mWorldMatrix, 0, mTempMatrix.length);
		}

		float[] inv = new float[16];
		Matrix.invertM(inv, 0, mWorldMatrix, 0);
		Matrix.multiplyMM(viewMatrix, 0, baseMatrix, 0, inv, 0);
	}
	
	protected void recalcMatrixReflect() {

		Matrix.setIdentityM(mWorldMatrix, 0);
		Matrix.setIdentityM(mTempMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mWorldMatrix, 0, mScaleMatrix, 0);
		Matrix.multiplyMM(mWorldMatrix, 0, mTempMatrix, 0, mRotMatrix, 0);
		Matrix.multiplyMM(mTempMatrix, 0, mWorldMatrix, 0, mTransMatrix, 0);

		if(parent != null) {
			//Only want to follow translation
			Matrix.multiplyMM(mWorldMatrix, 0, parent.getTransMatrix(), 0, mTempMatrix, 0);
		} else {
			System.arraycopy(mTempMatrix, 0, mWorldMatrix, 0, mTempMatrix.length);
		}

		float[] inv = new float[16];
		Matrix.invertM(inv, 0, mWorldMatrix, 0);
		Matrix.multiplyMM(viewMatrix, 0, baseMatrix, 0, inv, 0);
	}

	protected void recalcProjMatrix() {
		final float ratio = (float) viewport.x / viewport.y;

		final float left_ortho = -(viewport.x/2);
		final float right_ortho = (viewport.x/2);
		final float bottom_ortho = -(viewport.y/2);
		final float top_ortho = (viewport.y/2);

		//Rig for a 90 deg FOV
		Matrix.perspectiveM(projMatrix, 0, fov, ratio, near, far);
		Matrix.orthoM(orthoMatrix, 0, left_ortho, right_ortho, bottom_ortho, top_ortho, near, far);
	}
	
	public float[] getReflectionMatrix(float[] targetMatrix) {
		float[] camMatrix = getViewMatrix();
		
		float[] reflection = new float[]{
				camMatrix[0],
				camMatrix[1],
				camMatrix[2],
				camMatrix[3],
				
				camMatrix[4],
				camMatrix[5],
				camMatrix[6],
				camMatrix[7],
				
				camMatrix[8],
				camMatrix[9],
				camMatrix[10],
				camMatrix[11],
				
				camMatrix[12],
				-camMatrix[13],
				camMatrix[14],
				camMatrix[15],
		};
		
		Matrix.rotateM(reflection, 0, 180.0f, 1.0f, 0, 0);

		return reflection;
	}

}
