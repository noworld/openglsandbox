package com.solesurvivor.pthirtyeight.commands;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageBus;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageEnum;
import com.solesurvivor.pthirtyeight.input.InputEvent;
import com.solesurvivor.pthirtyeight.scene.gameobjects.GameObjectTypesEnum;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.PlayerFlying;
import com.solesurvivor.util.math.Vec3;

public class CreateEnemyCommand implements Command {
		
	@SuppressWarnings("unused")
	private static final String TAG = CreateEnemyCommand.class.getSimpleName();
	
	protected int targetState;
	protected int playerObject = 0;
	
	@Override
	public void onStateChanged() {
		targetState = GameWorld.inst().getCurrentState().getName().hashCode();
		playerObject = PlayerFlying.NAME.hashCode();
	}
	
	@Override
	public void execute(InputEvent event) {		
		SpriteNode enemy = new SpriteNode("enemy_1", GameWorld.SCALE, GameObjectTypesEnum.ENEMY);
		enemy.rotate(180.0f, new Vec3(0,0,1.0f));
		enemy.translateAnimation(new Vec3(100.0f, 100.0f, 0));
		GameMessageBus.dispatch(0, targetState, GameMessageEnum.CREATE_ENEMY, enemy);
	}

	@Override
	public void release(InputEvent event) {

	}
}
