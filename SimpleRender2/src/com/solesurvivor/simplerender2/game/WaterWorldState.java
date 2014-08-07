package com.solesurvivor.simplerender2.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.Matrix;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.solesurvivor.simplerender2.input.InputEventBus;
import com.solesurvivor.simplerender2.input.InputHandler;
import com.solesurvivor.simplerender2.loading.GameObjectLoader;
import com.solesurvivor.simplerender2.rendering.BaseRenderer;
import com.solesurvivor.simplerender2.rendering.GlSettings;
import com.solesurvivor.simplerender2.rendering.RendererManager;
import com.solesurvivor.simplerender2.scene.Light;
import com.solesurvivor.simplerender2.scene.Water;
import com.solesurvivor.simplerender2.text.Cursor;
import com.solesurvivor.simplerender2.text.FontManager;

public class WaterWorldState extends MainMenuState {
	
	private static final String TAG = WaterWorldState.class.getSimpleName();
	
	private float mAccumulatedRotation = 0.0f;
	private Cursor mLine1 = null;
	private Cursor mLine2 = null;
	private SortedWater mWater;
	
	public WaterWorldState() {
		Context ctx = GameGlobal.inst().getContext();
		TypedArray modelArray = ctx.getResources().obtainTypedArray(R.array.water_world_models);
		this.mObjectLibrary = GameObjectLoader.loadGameObjects(GameGlobal.inst().getContext(), modelArray);
		modelArray.recycle();
		mGlSettings = new GlSettings();
		mGlSettings.setGlClearColor(new float[]{0.681f, 0.886f, 1.0f, 1.0f});

		this.mObjectLibrary.mInputHandlers.add(GameGlobal.inst().getHandler(GlobalKeysEnum.BACK_BUTTON_INPUT_HANDLER));
		
		mLine1 = new Cursor(FontManager.getFont("Nightwatcher BB"), new float[]{1.5f,1.5f,1.0f},new float[]{-900.0f,480.0f,-4.0f},0,"");
		mLine2 = new Cursor(FontManager.getFont("Nightwatcher BB"), new float[]{1.5f,1.5f,1.0f},new float[]{-900.0f,420.0f,-4.0f},0,"");

		//Uncomment to draw text
//		this.mObjectLibrary.mCursors.add(mLine1);
//		this.mObjectLibrary.mCursors.add(mLine2);
//		
		mWater = new SortedWater();
	}

	@Override
	public void enter(GameWorld target) {
		Log.d(TAG, "Entering Water World State");
		RendererManager.inst().getRenderer().initOpenGL(mGlSettings);
		GameGlobal.inst().setCamera(this.mCamera);
	}

	@Override
	public void execute(GameWorld target) {
		//Log.d(TAG, "Executing Water World State");
		super.execute(target);
		mAccumulatedRotation += 0.5f;
		if(mAccumulatedRotation >= 360.0f) mAccumulatedRotation = 0.0f;
		for(Light light : mObjectLibrary.mLights) {
			Matrix.setIdentityM(light.mModelMatrix, 0);
			Matrix.translateM(light.mModelMatrix, 0, 0.0f, 0.0f, -5.0f);      
			Matrix.rotateM(light.mModelMatrix, 0, mAccumulatedRotation, 0.0f, 1.0f, 0.0f);
			Matrix.translateM(light.mModelMatrix, 0, 0.0f, 0.0f, 2.0f);
		}
		
		/*XXX FIRST PHYSICS! - Jump*/
		float dTSecs = ((float)mDeltaT) / (1000.0f);		
		mCameraTranslation[1] -= mCameraVelocity[1];
		mCameraVelocity[1] = Physics.applyGravity(mCameraVelocity[1], dTSecs);
		//Apply collision with imaginary plane
		if(mCameraTranslation[1] >= 0.0f) {
			mCameraTranslation[1] = 0.0f; //don't go below the y=0 plane
			mCameraVelocity[1] = 0.0f; //Stop when it hits the y=0 plane
		}
		
		mLine1.setValue(String.format("Near: %.1f,%.1f,%.1f",GameGlobal.inst().getCamera().getEyePos()[2], GameGlobal.inst().getCamera().getEyePos()[0], GameGlobal.inst().getCamera().getEyePos()[1]));
		mLine2.setValue(String.format("Aspect: %.2f",GameGlobal.inst().getCamera().getAspect()));
	}
	
	@Override
	public void render(GameWorld target) {		
		//TODO: Shift rendering to game objects and render back to front
		renderLights();
		renderModels();	
		renderWater();
		renderUi();
		renderOrthoText();
	}

	@Override
	public void exit(GameWorld target) {
		Log.d(TAG, "Exiting Water World State");
		for(InputHandler ih : mObjectLibrary.mInputHandlers) {
			ih.quiet();
		}
		//Clear input events when leaving the water world
		InputEventBus.inst().clear();
	}
	
	@Override
	public void translateView(float x, float y, float z) {
		mCameraTranslation[0] += z * Math.sin(Math.toRadians(mCameraRotation[0])) * -1;				
		mCameraTranslation[1] += y; //y-up
		mCameraTranslation[2] += z * Math.cos(Math.toRadians(mCameraRotation[0]));
	}

	@Override
	public void rotateView(float angle, float x, float y, float z) {
		mCameraRotation[0] += angle;
		
		if(mCameraRotation[0] > 360.0f) {
			mCameraRotation[0] = mCameraRotation[0] - 360.0f;
		} else if(mCameraRotation[0] < 0.0f) {
			mCameraRotation[0] = mCameraRotation[0] + 360.0f;
		}
		
		mCameraRotation[1] = (x + mCameraRotation[1])/2;
		mCameraRotation[2] = (y + mCameraRotation[2])/2;
		mCameraRotation[3] = (z + mCameraRotation[3])/2;
	}
	
	@Override
	public float[] getAgentViewMatrix() {
		float[] tempMatrix = new float[mCamera.getViewMatrix().length];
		System.arraycopy(mCamera.getViewMatrix(), 0, tempMatrix, 0, tempMatrix.length);
		Matrix.rotateM(tempMatrix, 0, mCameraRotation[0], mCameraRotation[1], mCameraRotation[2], mCameraRotation[3]);		
		Matrix.translateM(tempMatrix, 0, mCameraTranslation[0], mCameraTranslation[1], mCameraTranslation[2]);		
		
		return tempMatrix;
	}
	
	@Override
	public void impulseView(float x, float y, float z) {
		//Only allow impulse if we are at rest in y (on the ground)
		if(mCameraTranslation[1] == 0.0f) {
			super.impulseView(x, y, z);
		}
	}
	
	protected void renderWater() {
		BaseRenderer ren = RendererManager.inst().getRenderer();		
		
//		ren.setGlOptions(mWaterOptions);
		for(Water w : mObjectLibrary.mWaters) {
			ren.drawWater(w, mObjectLibrary.mLights);
		}
//		ren.setGlOptions(mGlSettings.getOptions());
		
		ren.drawWater(mWater.getWater(),  mObjectLibrary.mLights);
		
	}
	
}
