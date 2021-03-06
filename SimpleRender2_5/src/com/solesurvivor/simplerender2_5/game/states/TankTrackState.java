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
import com.solesurvivor.simplerender2_5.rendering.DrawingConstants;
import com.solesurvivor.simplerender2_5.rendering.GridMapRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;
import com.solesurvivor.simplerender2_5.scene.Actor;
import com.solesurvivor.simplerender2_5.scene.AnimatedNodeImpl;
import com.solesurvivor.simplerender2_5.scene.CameraNode;
import com.solesurvivor.simplerender2_5.scene.Curve;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.simplerender2_5.scene.GeometryBones;
import com.solesurvivor.simplerender2_5.scene.GeometryNode;
import com.solesurvivor.simplerender2_5.scene.MapGrid;
import com.solesurvivor.simplerender2_5.scene.PathAnimation;
import com.solesurvivor.simplerender2_5.scene.animation.Armature;
import com.solesurvivor.simplerender2_5.scene.animation.Pose;
import com.solesurvivor.simplerender2_5.scene.animation.PoseLibrary;
import com.solesurvivor.simplerender2_5.scene.nodestates.MatchHeadingWithDirectionState;
import com.solesurvivor.util.math.MatrixUtils;
import com.solesurvivor.util.math.Vec3;

public class TankTrackState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = TankTrackState.class.getSimpleName();
	
	protected CameraNode mCamera;
	protected Actor mActor;
	protected MapGrid mMapGrid;
	protected Pose startPose;
	protected Pose endPose;
	long startTime;
	long duration;
	
	public TankTrackState() {
		
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
						
			
			Curve trackCurve = GeometryIO.loadCurves(R.raw.trackcurve).get("track_path");
			trackCurve.translate(new Vec3(0,1.4f,0));
			PathAnimation anim = new PathAnimation(trackCurve, 10 * 1000);
			anim.setDrawPath(true);
			
			//TODO: Get the length of the curve in Python
			//and distribute evenly
			float displacement = 1.25f;
			Geometry trackLink = GeometryIO.loadGeometryMap(R.raw.trackcurve).get("track_link");
			trackLink.setTextureHandle(TextureManager.getTextureId("track_tex"));
			for(int i = 0; i < 73; i++) {				
				AnimatedNodeImpl trackLinkNode = new AnimatedNodeImpl(trackLink);
				trackLinkNode.setParent(anim);
				anim.addChild(trackLinkNode, i*displacement);
			}

			mScene.addChild(anim);

			Geometry actor = GeometryIO.loadGeometryMap(R.raw.rigged).get("Macho");

			mActor = new Actor(actor);
			mActor.changeState(new MatchHeadingWithDirectionState());
			mActor.rotate(180.0f, new Vec3(0,1.0f,0));
		
			mCamera = new CameraNode(eye, look, up, near, far, fov, mActor);		
			mCamera.resizeViewport(GameWorld.inst().getViewport());
			GridMapRenderer gmr = (GridMapRenderer)RendererManager.getRenderer();
			gmr.setCurrentCamera(mCamera);			
			
			mActor.addChild(mCamera);
			mScene.addChild(mActor);
			
			MapGrid mapGrid = new MapGrid();
			mScene.addChild(mapGrid);
			
			Map<String,PoseLibrary> animLibs = GeometryIO.lodePoseLibrary(R.raw.rigged);
			PoseLibrary poseLib = animLibs.get("Armature.003");
			Map<String,Armature> arms = GeometryIO.loadArmatureMap(R.raw.rigged2);
			Armature arm = arms.get("Armature.003");
//			Armature arm = arms.get("Arm.Cube");
//			Armature arm = arms.get("Arm.Box");
			
//			arm.updateBones();
			((GeometryBones)actor).setArmature(arm);
			((GeometryBones)actor).setPose(poseLib.getPose("ArmDown"));
			startPose = poseLib.getRestPose();
			endPose = poseLib.getPose("ArmUp");
			((GeometryBones)actor).setRestPose(poseLib.getRestPose());
			((GeometryBones)actor).setRestPoseInv(poseLib.getRestPoseInv());
			
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
		
		startTime = SystemClock.uptimeMillis();
		duration = 5000L;
	
	}
	
	@Override
	public void execute() {
		super.execute();

		if(SystemClock.uptimeMillis() - startTime >= duration) {
			startTime = SystemClock.uptimeMillis();
			((GeometryBones)mActor.getGeometry()).setPose(endPose);
			Pose temp = endPose;
			endPose = startPose;
			startPose = temp;
		}
		
		float[] animBones = new float[startPose.getBones().length];

		float dt = Math.min(1.0f, ((float)(SystemClock.uptimeMillis() - startTime)) / ((float)duration));

		for(int i = 0; i < startPose.getBones().length; i += DrawingConstants.FOURX_MATRIX_SIZE) {
			float[] m1 = new float[DrawingConstants.FOURX_MATRIX_SIZE];
			float[] m2 = new float[DrawingConstants.FOURX_MATRIX_SIZE];
			System.arraycopy(startPose.getBones(), i, m1, 0, DrawingConstants.FOURX_MATRIX_SIZE);
			System.arraycopy(endPose.getBones(), i, m2, 0, DrawingConstants.FOURX_MATRIX_SIZE);

			//			if(m1.equals(m2)) {
			//				System.arraycopy(m1, 0, animBones, i, DrawingConstants.FOURX_MATRIX_SIZE);
			//				continue;
			//			}

			float[] lerp = MatrixUtils.lerpMatrix(m1, m2, dt);
			System.arraycopy(lerp, 0, animBones, i, DrawingConstants.FOURX_MATRIX_SIZE);
		}

		((GeometryBones)mActor.getGeometry()).setPose(new Pose("Now",animBones));

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

