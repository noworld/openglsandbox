package com.solesurvivor.pthirtyeight.scene.gameobjects.behavior;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.scene.StatefulNodeImpl;
import com.solesurvivor.pthirtyeight.scene.gameobjects.Sprite;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteManager;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;

public class EnemyDeadAnimation implements NodeState {
	
	public static final String NAME = "PlayerFlyingState";
	
	@SuppressWarnings("unused")
	private static final String TAG = EnemyDeadAnimation.class.getSimpleName();
	private static final long ANIMATION_SPEED = 100; //ms per frame
	private static final String[] ANIMATION = new String[]{
			"enemy_boom_0",
			"enemy_boom_1",
			"enemy_boom_2",
			"enemy_boom_3",
			"enemy_boom_4",
			"enemy_boom_5"
		};
	private static final long TTL = ANIMATION_SPEED * ANIMATION.length;
	
	private long startTime = 0;
	private int currentFrame = -1;

	@Override
	public void enter(StatefulNodeImpl target) {
		this.startTime = GameWorld.inst().getGameT();
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		
		SpriteNode sn = (SpriteNode)target;

		int nextFrame = calcFrame();
		
		if(nextFrame != currentFrame) {
			Sprite s = SpriteManager.getSprite(ANIMATION[nextFrame]);
			sn.setSprite(s);
			currentFrame = nextFrame;
		}		
		
		if(startTime + TTL < GameWorld.inst().getGameT()) {
			sn.setAlive(false);
		}
	}	

	@Override
	public void exit(StatefulNodeImpl target) {
		
	}

	private int calcFrame() {
		return Math.min(ANIMATION.length - 1, (int)((GameWorld.inst().getGameT() - startTime) / ANIMATION_SPEED));
	}
}
