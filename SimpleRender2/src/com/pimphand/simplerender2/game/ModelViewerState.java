package com.pimphand.simplerender2.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.Matrix;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.pimphand.simplerender2.input.InputEventBus;
import com.pimphand.simplerender2.input.InputHandler;
import com.pimphand.simplerender2.loading.GameObjectLoader;
import com.pimphand.simplerender2.rendering.GlSettings;
import com.pimphand.simplerender2.rendering.RendererManager;
import com.pimphand.simplerender2.scene.Light;

public class ModelViewerState extends MainMenuState {
	
	private static final String TAG = ModelViewerState.class.getSimpleName();
	
	private float mAccumulatedRotation = 0.0f;
	
	public ModelViewerState() {
		Context ctx = GameGlobal.inst().getContext();
		TypedArray modelArray = ctx.getResources().obtainTypedArray(R.array.model_viewer_models);
		this.mObjectLibrary = GameObjectLoader.loadGameObjects(GameGlobal.inst().getContext(), modelArray);
		modelArray.recycle();
		mGlSettings = new GlSettings();
		this.mObjectLibrary.mInputHandlers.add(GameGlobal.inst().getHandler(GlobalKeysEnum.BACK_BUTTON_INPUT_HANDLER));
	}

	@Override
	public void enter(GameWorld target) {
		Log.d(TAG, "Entering Model Viewer State");
		RendererManager.inst().getRenderer().initOpenGL(mGlSettings);
	}

	@Override
	public void execute(GameWorld target) {
		//Log.d(TAG, "Executing Model Viewer State");
		mAccumulatedRotation += 0.5f;
		if(mAccumulatedRotation >= 360.0f) mAccumulatedRotation = 0.0f;
		for(Light light : mObjectLibrary.mLights) {
			Matrix.setIdentityM(light.mModelMatrix, 0);
			Matrix.translateM(light.mModelMatrix, 0, 0.0f, 0.0f, -5.0f);      
			Matrix.rotateM(light.mModelMatrix, 0, mAccumulatedRotation, 0.0f, 1.0f, 0.0f);
			Matrix.translateM(light.mModelMatrix, 0, 0.0f, 0.0f, 2.0f);
		}
	}

	@Override
	public void exit(GameWorld target) {
		Log.d(TAG, "Exiting Model Viewer State");
		for(InputHandler ih : mObjectLibrary.mInputHandlers) {
			ih.quiet();
		}
		//Clear input events when leaving the water world
		InputEventBus.inst().clear();
	}
	
	
}
