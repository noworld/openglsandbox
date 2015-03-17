package com.solesurvivor.simplerender2_5.game.states;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.commands.CommandEnum;
import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputUiElement;
import com.solesurvivor.simplerender2_5.io.GeometryIO;
import com.solesurvivor.simplerender2_5.rendering.GridMapRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Actor;
import com.solesurvivor.simplerender2_5.scene.CameraNode;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.simplerender2_5.scene.GeometryBones;
import com.solesurvivor.simplerender2_5.scene.MapGrid;
import com.solesurvivor.simplerender2_5.scene.animation.Armature;
import com.solesurvivor.simplerender2_5.scene.nodestates.MatchHeadingWithDirectionState;
import com.solesurvivor.util.math.Vec3;

public class GridMapState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = GridMapState.class.getSimpleName();
	
	protected CameraNode mCamera;
	protected Actor mActor;
	protected MapGrid mMapGrid;
	
	public GridMapState() {
		
		Vec3 eye = new Vec3(0.0f, 4.0f, 6.0f);
		Vec3 look = new Vec3(0.0f, 2.0f, -1.0f);
		Vec3 up = new Vec3(0.0f, 1.0f, 0.0f);
		float near = 1.0f;
		float far = 1000.0f;
		float fov = 50.0f;

		try {
			
			Vec3 pushback = new Vec3(0.0f, 0.0f, -5.0f);
			List<InputUiElement> uiElements = GeometryIO.loadInputUiElements(R.raw.gray_ui);		
			for(InputUiElement iu : uiElements) {
				iu.translate(pushback.getX(), pushback.getY(), pushback.getZ());
			}
			mInputHandlers.addAll(uiElements);
			mUi.addAll(GeometryIO.loadUiElements(R.raw.gray_ui));
			pushback = new Vec3(0.0f, 0.0f, -6.0f);
			for(Geometry g : mUi) {
				g.translate(pushback);
			}

//			Map<String,Geometry> tiles = GeometryIO.loadGeometryMap(R.raw.tile1);
//			
//			GeometryNode gn = new GeometryNode(tiles.get("tile"));
//			mScene.addChild(gn);
//			
//			GeometryNode gn2 = new GeometryNode(tiles.get("tile"));
//			gn2.translate(new Vec3(0.0f,0.0f,2.0f));
//			mScene.addChild(gn2);
			
//			Geometry arrow = GeometryIO.loadGeometryMap(R.raw.gray_ui).get("circle_arrow");
//			Geometry arrow = GeometryIO.loadGeometryMap(R.raw.rigged).get("TestObj");
			
			Geometry arrow = GeometryIO.loadGeometryMap(R.raw.rigged).get("Macho");
//			Geometry arrow = GeometryIO.loadGeometryMap(R.raw.rigged).get("Cube");
//			Geometry arrow = GeometryIO.loadGeometryMap(R.raw.rigged).get("Box");
			
			mActor = new Actor(arrow);
			mActor.changeState(new MatchHeadingWithDirectionState());
			
			mCamera = new CameraNode(eye, look, up, near, far, fov, mActor);		
			mCamera.resizeViewport(GameWorld.inst().getViewport());
			GridMapRenderer gmr = (GridMapRenderer)RendererManager.getRenderer();
			gmr.setCurrentCamera(mCamera);			
			
			mActor.addChild(mCamera);
			mScene.addChild(mActor);
			
			MapGrid mapGrid = new MapGrid();
			mScene.addChild(mapGrid);
			
			Map<String,Armature> arms = GeometryIO.loadArmatureMap(R.raw.rigged);
			Armature arm = arms.get("Armature.003");
//			Armature arm = arms.get("Arm.Cube");
//			Armature arm = arms.get("Arm.Box");
			
//			arm.updateBones();
			((GeometryBones)arrow).setArmature(arm);
			
//			float[] mat = new float[16];
//			Matrix.setIdentityM(mat, 0);
//			Matrix.translateM(mat, 0, 0.02583f, -0.63138f, -0.30472f);
//			for(int i = 0; i < mat.length; i++) {
//				SSLog.d(TAG, "MATRIX INDEX %s: %s", i, mat[i]);
//			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void enter() {
//		RendererManager.getRenderer().setCurrentCamera(mCamera);
		RendererManager.getRenderer().initOpenGLDefault();
		for(CommandEnum ce : CommandEnum.values()) {
			ce.getCommand().onStateChanged();
		}
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
		mActor.rotate(angle, rot);
	}

	@Override
	public void translateCurrentCamera(Vec3 trans) {
		mActor.translate(trans);
	}

}
