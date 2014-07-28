package com.pimphand.simplerender2.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.pimphand.simplerender2.fsm.GameState;
import com.pimphand.simplerender2.input.InputEventBus;
import com.pimphand.simplerender2.input.InputHandler;
import com.pimphand.simplerender2.loading.GameObjectLoader;
import com.pimphand.simplerender2.rendering.BaseRenderer;
import com.pimphand.simplerender2.rendering.GlSettings;
import com.pimphand.simplerender2.rendering.RendererManager;
import com.pimphand.simplerender2.scene.GameEntity;
import com.pimphand.simplerender2.scene.GameObjectLibrary;
import com.pimphand.simplerender2.scene.Light;
import com.pimphand.simplerender2.scene.UiElement;

public class MainMenuState implements GameState<GameWorld> {
	
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	protected float[] mMVPMatrix = new float[16]; 
	protected GameObjectLibrary mObjectLibrary;
	protected GlSettings mGlSettings;
	protected boolean mDrawInputAreas = false;
	
	public MainMenuState() {
		Context ctx = GameGlobal.inst().getContext();
		TypedArray modelArray = ctx.getResources().obtainTypedArray(R.array.main_menu_models);
		this.mObjectLibrary = GameObjectLoader.loadGameObjects(GameGlobal.inst().getContext(), modelArray);
		modelArray.recycle();
		this.mObjectLibrary.mInputHandlers.add(GameGlobal.inst().getHandler(GlobalKeysEnum.BACK_BUTTON_INPUT_HANDLER));
		mGlSettings = new GlSettings();
		mGlSettings.setGlClearColor(new float[]{0.6f, 0.4f, 0.2f, 1.0f});
		mDrawInputAreas = GameGlobal.inst().getBool(GlobalKeysEnum.DRAW_INPUT_AREAS);
	}

	@Override
	public void enter(GameWorld target) {
		Log.d(TAG, "Entering Main Menu State");
		RendererManager.inst().getRenderer().initOpenGL(mGlSettings);
	}

	@Override
	public void execute(GameWorld target) {
		
	}

	@Override
	public void render(GameWorld target) {
		//Log.d(TAG, "Executing Main Menu State");
		renderLights();
		renderModels();		
		renderUi();
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
			ren.drawGeometry(ge.getGeometry(), this.getLibrary().mLights);
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

}
