package com.pimphand.simplerender2.game;

import android.graphics.Point;

import com.pimphand.simplerender2.fsm.State;
import com.pimphand.simplerender2.input.InputEventBus;
import com.pimphand.simplerender2.input.InputHandler;
import com.pimphand.simplerender2.rendering.BaseRenderer;
import com.pimphand.simplerender2.rendering.RendererManager;
import com.pimphand.simplerender2.scene.Camera;
import com.pimphand.simplerender2.scene.GameEntity;
import com.pimphand.simplerender2.scene.UiElement;

public class GameWorld {

	private static GameWorld sInstance;

	private Camera mCamera;
	private State<GameWorld> mCurrentState;
	private State<GameWorld> mPreviousState;
	private boolean mDrawInputAreas = false;

	private GameWorld() {
		this.mCamera = new Camera();
	}

	public static GameWorld inst() {
		return sInstance;
	}
	
	public Camera getCamera() {
		return mCamera;
	}
	
	public static void init() {
		sInstance = new GameWorld();
		sInstance.changeState(GameStateManager.getState(GameStateEnum.MAIN_MENU));
	}
	
	public void update() {

		InputEventBus.inst().executeCommands(mCurrentState.getLibrary().mInputHandlers);
		
		this.mCurrentState.execute(this);
	}

	public void render() {		
		renderUi();
	}

	public boolean changeState(State<GameWorld> state) {

		if(mCurrentState != null) {
			mCurrentState.exit(this);
			this.mPreviousState = mCurrentState;
		}
		
		mCurrentState = state;
		mCurrentState.enter(this);

		return true;

	}
	
	public boolean revertState() {
		return changeState(mPreviousState);
	}
	
	public void resizeViewport(Point point) {
		this.mCamera.resizeViewport(point);
	}

	private void renderUi() {
		
		BaseRenderer ren = RendererManager.inst().getRenderer();
		
		for(GameEntity ge : mCurrentState.getLibrary().mEntities) {
			ren.drawUI(ge.getGeometry());
		}
		
		for(UiElement ui : mCurrentState.getLibrary().mDisplayElements) {
			ren.drawUI(ui.getGeometry());
		}
		
		if(mDrawInputAreas) {
			//If an input handler can be drawn then draw it
			for(InputHandler ih : mCurrentState.getLibrary().mInputHandlers) {
				if(ih instanceof UiElement) {
					ren.drawUI(((UiElement)ih).getGeometry());
				}
			}
		}
	}
	
}
