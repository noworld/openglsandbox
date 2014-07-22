package com.pimphand.simplerender2.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.pimphand.simplerender2.input.InputHandler;
import com.pimphand.simplerender2.loading.GeometryLoader;
import com.pimphand.simplerender2.rendering.GlSettings;
import com.pimphand.simplerender2.rendering.RendererManager;

public class WaterWorldState extends MainMenuState {
	
	private static final String TAG = WaterWorldState.class.getSimpleName();
	
	public WaterWorldState() {
		Context ctx = GameGlobal.inst().getContext();
		TypedArray modelArray = ctx.getResources().obtainTypedArray(R.array.main_menu_models);
		this.mObjectLibrary = GeometryLoader.loadGameObjects(GameGlobal.inst().getContext(), modelArray);
		modelArray.recycle();
		mGlSettings = new GlSettings();
		this.mObjectLibrary.mInputHandlers.add(GameGlobal.inst().getHandler(GlobalKeysEnum.BACK_BUTTON_INPUT_HANDLER));
	}

	@Override
	public void enter(GameWorld target) {
		Log.d(TAG, "Entering Water World State");
		RendererManager.inst().getRenderer().initOpenGL(mGlSettings);
	}

	@Override
	public void execute(GameWorld target) {
		//Log.d(TAG, "Executing Water World State");
	}

	@Override
	public void exit(GameWorld target) {
		Log.d(TAG, "Exiting Water World State");
		for(InputHandler ih : mObjectLibrary.mInputHandlers) {
			ih.quiet();
		}
	}
	
	
}
