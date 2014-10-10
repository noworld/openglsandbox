package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.commands.CommandEnum;
import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.game.GlobalKeysEnum;
import com.solesurvivor.simplerender2_5.input.BackButtonInputHandler;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.CoordinateSystemEnum;
import com.solesurvivor.simplerender2_5.scene.Plane;
import com.solesurvivor.simplerender2_5.scene.ProceduralTexture2D;
import com.solesurvivor.util.math.Vec3;

public class SkyGradientState extends BaseState {
	
	protected Camera mCamera;
	
	public SkyGradientState() {
		Point viewport = GameWorld.inst().getViewport();
		mCamera = new Camera();		
		mCamera.resizeViewport(viewport);
		float[] eyePos = mCamera.getEyePos();
		float[] lookVec = mCamera.getLookVector();
		mCamera.setEyePos(eyePos);
		mCamera.setLookVector(lookVec);
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		
		ProceduralTexture2D skyTex = new ProceduralTexture2D("skytex_shader", "skytex", new Point(viewport.y,viewport.y), CoordinateSystemEnum.CARTESIAN);
		mScene.addChild(skyTex);
		
		Plane plane = new Plane("plane_shader", "skytex", new Point(viewport.y,viewport.y));
		mScene.addChild(plane);
		
		BackButtonInputHandler bbih = new BackButtonInputHandler();
		bbih.registerCommand(CommandEnum.REVERT_STATE.getCommand());
		mInputHandlers.add(bbih);
		
	}

	@Override
	public void enter() {
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		RendererManager.getRenderer().initOpenGLDefault();
	}

	@Override
	public void resizeViewport(Point p) {
		mCamera.resizeViewport(p);
	}

	@Override
	public void rotateCurrentCamera(float angle, Vec3 rot) {
		mCamera.rotate(angle, rot);
	}

	@Override
	public void translateCurrentCamera(Vec3 trans) {
		mCamera.translate(trans);
	}

}
