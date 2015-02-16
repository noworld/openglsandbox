package com.solesurvivor.simplerender2_5.game.states;

import java.io.IOException;
import java.util.List;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.commands.CommandEnum;
import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.game.GlobalKeysEnum;
import com.solesurvivor.simplerender2_5.input.BackButtonInputHandler;
import com.solesurvivor.simplerender2_5.input.InputUiElement;
import com.solesurvivor.simplerender2_5.io.GeometryIO;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.CoordinateSystemEnum;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.simplerender2_5.scene.GeometryNode;
import com.solesurvivor.simplerender2_5.scene.ProceduralTexture2D;
import com.solesurvivor.simplerender2_5.scene.Skydome;
import com.solesurvivor.simplerender2_5.scene.Water;
import com.solesurvivor.util.math.Vec3;

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
		eyePos[2] += 10.0f;
		lookVec[1] += camHeight;
		mCamera.setEyePos(eyePos);
		mCamera.setLookVector(lookVec);
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		
//		Skybox skybox = new Skybox("skybox_shader", "tenerife_cube");
//		mScene.addChild(skybox);		
		
		try {
			ProceduralTexture2D skyTex = new ProceduralTexture2D("skytex_shader", "skytex", new Point(viewport.y,viewport.y), CoordinateSystemEnum.CARTESIAN);
			mScene.addChild(skyTex);
			
			Skydome dome = new Skydome(GeometryIO.loadGeometry(R.raw.skydome).get(0));
			dome.switchTexture("skytex");
			mScene.addChild(dome);
			
//			TerrainClipmap clipMap = GeometryIO.loadClipmap(R.raw.geoclip);
//			mScene.addChild(clipMap);
			
			Water water = new Water();
			mScene.addChild(water);
			
			
			Vec3 pushback = new Vec3(0.0f, 0.0f, -5.0f);
			List<InputUiElement> uiElements = GeometryIO.loadInputUiElements(R.raw.blue_ui);		
			for(InputUiElement iu : uiElements) {
				iu.translate(pushback.getX(), pushback.getY(), pushback.getZ());
			}
			mInputHandlers.addAll(uiElements);
			mUi.addAll(GeometryIO.loadUiElements(R.raw.blue_ui));
			pushback = new Vec3(0.0f, 0.0f, -6.0f);
			for(Geometry g : mUi) {
				g.translate(pushback);
			}
			
			List<Geometry> ships = GeometryIO.loadGeometry(R.raw.ohp);
			GeometryNode ohp = new GeometryNode(ships.get(0));
			ohp.translate(new Vec3(0.0f, 0.0f, -1.0f));
			mScene.addChild(ohp);
			
			BackButtonInputHandler bbih = new BackButtonInputHandler();
			bbih.registerCommand(CommandEnum.REVERT_STATE.getCommand());
			mInputHandlers.add(bbih);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void enter() {
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		RendererManager.getRenderer().initOpenGLDefault();
	}
	
	@Override
	public void execute() {
		super.execute();
	}
	
	@Override
	public void render() {		
		super.render();
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
