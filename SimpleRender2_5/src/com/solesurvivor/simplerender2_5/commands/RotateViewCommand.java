package com.solesurvivor.simplerender2_5.commands;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;

public class RotateViewCommand implements Command {
	
	protected static final float DELTA_H = 0.5f;
	protected static final float DELTA_R = 0.1f;
	protected static final float DELTA_Z = 0.1f;
	protected float[] mMat = new float[16];
	
	public RotateViewCommand() {
		Matrix.setIdentityM(mMat, 0);
	}

//	@Override
//	public void execute(InputEvent event) {
//		Camera cam = RendererManager.getRenderer().getCurrentCamera();
//		float[] eye = cam.getEyePos();
//		float[] look = cam.getLookVector();
////		cam.setEyePos(new float[]{eye[0], eye[1]+DELTA_H, eye[2]});	
//		Matrix.rotateM(mMat, 0, DELTA_R, 0.0f, 1.0f, 0.0f);
//		float[] newLook = new float[4];
//		float[] look2 = {look[0], look[1], look[2], 1.0f};
//		Matrix.multiplyMV(newLook, 0, mMat, 0, look2, 0);
//		cam.setLookVector(new float[]{newLook[0], newLook[1], newLook[2]});
//	}
	
	@Override
	public void execute(InputEvent event) {
		Camera cam = RendererManager.getRenderer().getCurrentCamera();
		float[] eye = cam.getEyePos();
//		float[] look = cam.getLookVector();
//		cam.setEyePos(new float[]{eye[0], eye[1]+DELTA_H, eye[2]});
		//cam.setEyePos(new float[]{eye[0], eye[1], eye[2] + DELTA_Z});
//		cam.setLookVector(new float[]{0.0f, 2.0f, -1.0f});
	}

	@Override
	public void onStateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void release(InputEvent event) {
		// TODO Auto-generated method stub
		
	}

}
