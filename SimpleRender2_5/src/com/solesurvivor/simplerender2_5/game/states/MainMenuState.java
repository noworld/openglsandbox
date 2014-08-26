package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.Skybox;

public class MainMenuState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	protected Camera mCamera;
	
	public MainMenuState() {
		mCamera = new Camera();
		int skyboxTexture = TextureManager.getTextureId("tenerife_cube");
		int skyboxShader = ShaderManager.getShaderId("skybox_shader");
		Skybox skybox = new Skybox(skyboxShader, skyboxTexture);
		mScene.addChild(skybox);
	}

	@Override
	public void enter() {
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		RendererManager.getRenderer().initOpenGLDefault();
	}
	
	@Override
	public void execute() {
		float[] camRot = mCamera.getRotation();
		float rotation = camRot[0] + 0.5f;
		
		if(rotation > 360.0f) {
			rotation = rotation - 360.0f;
		} else if(rotation < 0.0f) {
			rotation = rotation + 360.0f;
		}
		
		camRot[0] = rotation;
		camRot[2] = 1.0f;
		mCamera.setRotation(camRot);
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
