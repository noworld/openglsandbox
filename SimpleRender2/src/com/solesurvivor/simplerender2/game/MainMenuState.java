package com.solesurvivor.simplerender2.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.solesurvivor.simplerender2.fsm.GameState;
import com.solesurvivor.simplerender2.input.InputEventBus;
import com.solesurvivor.simplerender2.input.InputHandler;
import com.solesurvivor.simplerender2.loading.GameObjectLoader;
import com.solesurvivor.simplerender2.rendering.BaseRenderer;
import com.solesurvivor.simplerender2.rendering.GlSettings;
import com.solesurvivor.simplerender2.rendering.RendererManager;
import com.solesurvivor.simplerender2.scene.Camera;
import com.solesurvivor.simplerender2.scene.GameEntity;
import com.solesurvivor.simplerender2.scene.GameObjectLibrary;
import com.solesurvivor.simplerender2.scene.Light;
import com.solesurvivor.simplerender2.scene.UiElement;
import com.solesurvivor.simplerender2.text.Cursor;
import com.solesurvivor.util.math.Vec3;

public class MainMenuState implements GameState<GameWorld> {
	
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	protected float[] mMVPMatrix = new float[16]; 
	protected GameObjectLibrary mObjectLibrary;
	protected GlSettings mGlSettings;
	protected boolean mDrawInputAreas = false;
	protected long mDeltaT = 0L;
	protected long mLastT = SystemClock.uptimeMillis(); 
	protected Camera mCamera;
	protected float[] mCameraTranslation = {0.0f, 0.0f, 0.0f};
	protected float[] mCameraRotation = {0.0f, 0.0f, 0.0f, 1.0f};
	protected float[] mCameraVelocity = {0.0f, 0.0f, 0.0f};
	
	public MainMenuState() {
		Context ctx = GameGlobal.inst().getContext();
		TypedArray modelArray = ctx.getResources().obtainTypedArray(R.array.main_menu_models);
		this.mObjectLibrary = GameObjectLoader.loadGameObjects(GameGlobal.inst().getContext(), modelArray);
		modelArray.recycle();
		this.mObjectLibrary.mInputHandlers.add(GameGlobal.inst().getHandler(GlobalKeysEnum.BACK_BUTTON_INPUT_HANDLER));
		mGlSettings = new GlSettings();
		mGlSettings.setGlClearColor(new float[]{0.6f, 0.4f, 0.2f, 1.0f});
		mDrawInputAreas = GameGlobal.inst().getBool(GlobalKeysEnum.DRAW_INPUT_AREAS);
		this.mCamera = new Camera();
	}

	@Override
	public void enter(GameWorld target) {
		Log.d(TAG, "Entering Main Menu State");
		RendererManager.inst().getRenderer().initOpenGL(mGlSettings);
	}

	@Override
	public void execute(GameWorld target) {
		long tempT = SystemClock.uptimeMillis();
		mDeltaT = tempT - mLastT;
		mLastT = tempT;
	}

	@Override
	public void render(GameWorld target) {
		//Log.d(TAG, "Executing Main Menu State");
		renderLights();
		renderModels();		
		renderUi();
		renderOrthoText();
	}

	protected void renderOrthoText() {
		BaseRenderer ren = RendererManager.inst().getRenderer();
		for(Cursor c : this.getLibrary().mCursors) {
			ren.drawText(c);
		}
	}

	@Override
	public void exit(GameWorld target) {
		Log.d(TAG, "Exiting Main Menu State");
		for(InputHandler ih : mObjectLibrary.mInputHandlers) {
			ih.quiet();
		}
		//Clear input events when leaving the main menu
		InputEventBus.inst().clear();
	}

	@Override
	public GameObjectLibrary getLibrary() {
		return mObjectLibrary;
	}
	
	protected void renderModels() {
		BaseRenderer ren = RendererManager.inst().getRenderer();
		
		for(GameEntity ge : this.getLibrary().mEntities) {
			if(ge.getGeometry().mName.equals("plane"))  {
				ren.drawGeometryMesh(ge.getGeometry(), this.getLibrary().mLights);
			} else {
				ren.drawGeometry(ge.getGeometry(), this.getLibrary().mLights);
			}
		}
	}
	
	protected void renderLights() {
		BaseRenderer ren = RendererManager.inst().getRenderer();
		
		for(Light light : this.getLibrary().mLights) {
			ren.drawLight(light);
		}
	}

	protected void renderUi() {
		
		BaseRenderer ren = RendererManager.inst().getRenderer();
		
		for(UiElement ui : this.getLibrary().mDisplayElements) {
			ren.drawUI(ui.getGeometry());
			ren.drawText(ui.getCursor());
		}
		
		if(mDrawInputAreas) {
			//If an input handler can be drawn then draw it
			for(InputHandler ih : this.getLibrary().mInputHandlers) {
				if(ih instanceof UiElement) {
					ren.drawUI(((UiElement)ih).getGeometry());
				}
			}
		}
	}
	
	@Override
	public float getDistanceToCamera(Vec3 point) {
		return 0.0f;
	}

	@Override
	public void resizeViewport(Point point) {
		this.mCamera.resizeViewport(point);
	}
	
	@Override
	public float[] getAgentViewRotation() {
		return mCameraRotation;
	}

	@Override
	public void translateView(float x, float y, float z) {

	}

	@Override
	public void rotateView(float angle, float x, float y, float z) {

	}

	@Override
	public float[] getUiMatrix() {
		return mCamera.getUiMatrix();
	}

	@Override
	public float[] getViewMatrix() {
		return mCamera.getViewMatrix();
	}

	@Override
	public float[] getProjectionMatrix() {
		return mCamera.getProjectionMatrix();
	}

	@Override
	public void impulseView(float x, float y, float z) {
		mCameraVelocity[0] += x;
		mCameraVelocity[1] += y;
		mCameraVelocity[2] += z;
	}

	@Override
	public float[] getAgentViewMatrix() {
		return mCamera.getViewMatrix();
	}

}
