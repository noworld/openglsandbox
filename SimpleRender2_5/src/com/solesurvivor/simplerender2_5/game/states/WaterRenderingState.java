package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.game.GlobalKeysEnum;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.SeaTilesManager;
import com.solesurvivor.simplerender2_5.scene.Skybox;

public class WaterRenderingState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = WaterRenderingState.class.getSimpleName();
	
	protected Camera mCamera;
	
	public WaterRenderingState() {
		Point viewport = GameWorld.inst().getViewport();
		mCamera = new Camera();		
		mCamera.resizeViewport(viewport);
		float camHeight = Float.valueOf(GameGlobal.inst().getVal(GlobalKeysEnum.INITIAL_CAMERA_HEIGHT));
		float[] eyePos = mCamera.getEyePos();
		float[] lookVec = mCamera.getLookVector();
		eyePos[1] += camHeight;
		lookVec[1] += camHeight;
		mCamera.setEyePos(eyePos);
		mCamera.setLookVector(lookVec);
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		
		Skybox skybox = new Skybox("skybox_shader", "tenerife_cube");
		mScene.addChild(skybox);
		
		mScene.addChild(new SeaTilesManager());
	}

	@Override
	public void enter() {
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		RendererManager.getRenderer().initOpenGLDefault();
	}
	
	@Override
	public void execute() {
		super.execute();
		float angle = GameWorld.inst().getDeltaT() / 1000.0f * 5.0f;
		float[] rot = mCamera.getRotation();
		float[] newRot = {rot[0] + angle, 0.0f, 1.0f, 0.0f};
		mCamera.setRotation(newRot);
	}
	
	@Override
	public void render() {		
		super.render();
	}
	
	@Override
	public void resizeViewport(Point p) {
		mCamera.resizeViewport(p);
	}
	
}
