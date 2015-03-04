package com.solesurvivor.pthirtyeight.commands;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageBus;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageEnum;
import com.solesurvivor.pthirtyeight.input.InputEvent;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.PlayerFlying;

public class FireCommand implements Command {
		
	@SuppressWarnings("unused")
	private static final String TAG = FireCommand.class.getSimpleName();
	
	protected int targetState;
	protected int playerObject = 0;
	private long delay = 100;
	private long lastEvent = 0;
	
	@Override
	public void onStateChanged() {
		targetState = GameWorld.inst().getCurrentState().getName().hashCode();
		playerObject = PlayerFlying.NAME.hashCode();
	}
	
	@Override
	public void execute(InputEvent event) {		
		if(GameWorld.inst().getGameT() > lastEvent + delay) {
			GameMessageBus.dispatch(0, playerObject, GameMessageEnum.FIRE);
			lastEvent = GameWorld.inst().getGameT();
		}
	}

	@Override
	public void release(InputEvent event) {

	}
}
