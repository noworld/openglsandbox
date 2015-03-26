package com.solesurvivor.simplerender2_5.game.states;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.graphics.Point;
import android.os.SystemClock;

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
import com.solesurvivor.simplerender2_5.scene.GeometryNode;
import com.solesurvivor.simplerender2_5.scene.MapGrid;
import com.solesurvivor.simplerender2_5.scene.Skybox;
import com.solesurvivor.simplerender2_5.scene.WaterNode;
import com.solesurvivor.simplerender2_5.scene.animation.Pose;
import com.solesurvivor.simplerender2_5.scene.nodestates.MatchHeadingWithDirectionState;
import com.solesurvivor.util.math.Vec3;

public class GridMapState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = GridMapState.class.getSimpleName();
	
	protected CameraNode mCamera;
	protected Actor mActor;
	protected MapGrid mMapGrid;
	protected Pose startPose;
	protected Pose endPose;
	long startTime;
	long duration;
	
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
			
			Map<String,Geometry> island = GeometryIO.loadGeometryMap(R.raw.island); 
			Geometry arrow = island.get("Arrow");

			mActor = new Actor(arrow);
			mActor.changeState(new MatchHeadingWithDirectionState());
			mActor.rotate(180.0f, new Vec3(0,1.0f,0));
		
			mCamera = new CameraNode(eye, look, up, near, far, fov, mActor);		
			mCamera.resizeViewport(GameWorld.inst().getViewport());
			GridMapRenderer gmr = (GridMapRenderer)RendererManager.getRenderer();
			gmr.setCurrentCamera(mCamera);			
			mCamera.setParent(mActor);
			
//			mCamera.rotate(-30.0f, new Vec3(1.0f,0.0f,0.0f));
			
			mActor.addChild(mCamera);
			mScene.addChild(mActor);
			
			Skybox skybox = new Skybox("skybox_shader", "tenerife_etc1");
			mScene.addChild(skybox);	
			
			Geometry terrain = island.get("Landscape");
			mScene.addChild(new GeometryNode(terrain));
			
			Geometry ocean = island.get("Water");
			WaterNode wn = new WaterNode(ocean);
			wn.setSkybox(skybox);
			mScene.addChild(wn);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void enter() {
		RendererManager.getRenderer().initOpenGLDefault();
		for(CommandEnum ce : CommandEnum.values()) {
			ce.getCommand().onStateChanged();
		}
		
		GridMapRenderer gmr = (GridMapRenderer)RendererManager.getRenderer();
		gmr.setCurrentCamera(mCamera);
		
		startTime = SystemClock.uptimeMillis();
		duration = 5000L;
	
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

