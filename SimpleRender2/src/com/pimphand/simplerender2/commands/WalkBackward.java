package com.pimphand.simplerender2.commands;

import com.pimphand.simplerender2.fsm.GameState;
import com.pimphand.simplerender2.game.GameStateEnum;
import com.pimphand.simplerender2.game.GameStateManager;
import com.pimphand.simplerender2.game.GameWorld;
import com.pimphand.simplerender2.input.InputEvent;
import com.pimphand.simplerender2.scene.Camera;

public class WalkBackward implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = WalkBackward.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameState<GameWorld> ww = GameStateManager.getState(GameStateEnum.WATER_WORLD);
		Camera c = ww.getCamera();
		c.translate(0.0f, 0.0f, -0.01f);
	}
}
