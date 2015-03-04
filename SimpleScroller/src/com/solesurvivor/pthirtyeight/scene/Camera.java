package com.solesurvivor.pthirtyeight.scene;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class Camera {
	
	private static final String TAG = Camera.class.getSimpleName();

	protected float[] viewMatrix = new float[16];
	protected float[] agentMatrix = new float[16];
	protected boolean viewDirty = false;
	protected float[] projectionMatrix  = new float[16];
	protected float[] orthoMatrix = new float[16];
//	protected float[] eyePos = {0.0f, 2.0f, 0.0f};
//	protected float[] lookVector = {0.0f, 2.0f, -1.0f};
//	protected float[] upVector = {0.0f, 1.0f, 0.0f};
	protected float[] eyePos = {0.0f, 0.0f, 1.0f};
	protected float[] lookVector = {0.0f, 0.0f, -1.0f};
	protected float[] upVector = {0.0f, 1.0f, 0.0f};	
	protected Point viewport = new Point(0,0);
	protected float near = 1.0f;
	protected float far = 1000.0f; //200.0f;
	protected float fov = 50.0f;
	
	protected float rotAngle = 0.0f;
	protected Vec3 rotAxes = new Vec3(0.0f,1.0f,0.0f);
	protected Vec3 trans = new Vec3(0.0f,0.0f,0.0f);
	protected float[] velocity = {0.0f, 0.0f, 0.0f};
	
	
	public Camera() {		
		orient();
	}

	public float[] getEyePos() {
		return eyePos;
	}

	public void setEyePos(float[] eyePos) {
		this.eyePos = eyePos;
		viewDirty = true;
	}

	public float[] getLookVector() {
		return lookVector;
	}

	public void setLookVector(float[] lookVector) {
		this.lookVector = lookVector;
		viewDirty = true;
	}

	public float[] getUpVector() {
		return upVector;
	}

	public void setUpVector(float[] upVector) {
		this.upVector = upVector;
		viewDirty = true;
	}
	
	public Point getViewport() {
		return viewport;
	}
	
	public float[] getViewMatrix() {
		if(viewDirty) {
			orient();
		}
		return viewMatrix;
	}
	
	public float[] getAgentViewMatrix() {
		if(viewDirty) {
			orient();
		}
		return agentMatrix;
	}
	
	public float[] getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public float[] getOrthoMatrix() {
		return orthoMatrix;
	}

	protected void orient() {
		Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], 
				lookVector[0], lookVector[1], lookVector[2],
				upVector[0], upVector[1], upVector[2]);
		
		float[] transforms = new float[viewMatrix.length];
		Matrix.setIdentityM(transforms, 0);
		Matrix.rotateM(transforms, 0, rotAngle, rotAxes.getX(), rotAxes.getY(), rotAxes.getZ());		
		Matrix.translateM(transforms, 0, trans.getX(), trans.getY(), trans.getZ());
		
		Matrix.multiplyMM(agentMatrix, 0, viewMatrix, 0, transforms, 0);
		
		viewDirty = false;
	}
	
	public float getFar() {
		return this.far;
	}
	

	public float getNear() {
		return this.near;
	}
	
	public void resizeViewport(Point newViewport) {
		if(newViewport == null) {
			SSLog.d(TAG, "Null viewport sent to camera.");
			return;
		} else if(!viewport.equals(newViewport)) {
			SSLog.d(TAG, "Resizing camera viewport: %s,%s", newViewport.x, newViewport.y);
			this.viewport = newViewport;

			final float ratio = (float) viewport.x / viewport.y;

			final float left_ortho = -(viewport.x/2);
			final float right_ortho = (viewport.x/2);
			final float bottom_ortho = -(viewport.y/2);
			final float top_ortho = (viewport.y/2);
			
			//Rig for a 90 deg FOV
			Matrix.perspectiveM(projectionMatrix, 0, fov, ratio, near, far);
			Matrix.orthoM(orthoMatrix, 0, left_ortho, right_ortho, bottom_ortho, top_ortho, near, far);
		} else {
			SSLog.d(TAG, "Keeping viewport size");
		}

	}

	public float[] getVelocity() {
		return velocity;
	}

	public void setVelocity(float[] velocity) {
		this.velocity = velocity;
		viewDirty = true;
	}
	
	public void rotate(float angle, Vec3 axes) {
		rotAngle += angle;

		if(rotAngle > 360.0f) {
			rotAngle = rotAngle - 360.0f;
		} else if(rotAngle < 0.0f) {
			rotAngle = rotAngle + 360.0f;
		}

		rotAxes.setX((axes.getX() + rotAxes.getX())/2);
		rotAxes.setY((axes.getY() + rotAxes.getY())/2);
		rotAxes.setZ((axes.getZ() + rotAxes.getZ())/2);
		viewDirty = true;
	}
	
	public void translate(Vec3 translation) {
		trans.setX((float)(trans.getX() + (translation.getZ() * Math.sin(Math.toRadians(rotAngle)) * -1)));
		trans.setX((float)(trans.getX() + (translation.getX() * Math.sin(Math.toRadians(rotAngle + 90)) * -1)));
		trans.setY((float)(trans.getY() + (translation.getY()))); //y-up
		trans.setZ((float)(trans.getZ() + (translation.getZ() * Math.cos(Math.toRadians(rotAngle)))));
		trans.setZ((float)(trans.getZ() + (translation.getX() * Math.cos(Math.toRadians(rotAngle + 90)))));
		viewDirty = true;
	}
	
	public Vec3 getAgentTranslation() {
		return trans;
	}

}
