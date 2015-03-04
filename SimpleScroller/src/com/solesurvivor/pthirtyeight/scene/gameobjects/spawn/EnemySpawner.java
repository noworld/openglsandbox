package com.solesurvivor.pthirtyeight.scene.gameobjects.spawn;

import java.util.Random;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageBus;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageEnum;
import com.solesurvivor.pthirtyeight.game.states.PlayState;
import com.solesurvivor.pthirtyeight.physics.EnemyPhysics;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.pthirtyeight.scene.Node;
import com.solesurvivor.pthirtyeight.scene.gameobjects.GameObjectTypesEnum;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.FastSeekBehavior;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.SlowSeekBehavior;
import com.solesurvivor.util.math.Vec3;

public class EnemySpawner {
	
	private static final long INTERVAL = 1000; //ms
	
	private PlayState targetState;
	private long lastT = 0; 
	private Node player;
	private float screenWidth;
	private float halfWidth;
	private Random rand;
	protected boolean on = false;
	
	public EnemySpawner(Node player) {
		this.player = player;
		targetState = (PlayState)GameWorld.inst().getCurrentState();
		screenWidth = RendererManager.getViewport().x / GameWorld.SCALE;
		halfWidth = screenWidth / 2;
		rand = new Random();
	}
	
	public void setPlayer(Node player) {
		this.player = player;
	}
	
	public void update() {
		
		if(on) {
			lastT = lastT + GameWorld.inst().getDeltaT();
			if(lastT > INTERVAL) {
				lastT = 0;
				
				float dx = (rand.nextFloat() * screenWidth) - halfWidth;
				float dy = (rand.nextFloat() * 75.0f) + 75.0f;
				float fastSlow = rand.nextFloat();
				long randomDumb = (long)((rand.nextFloat() * 2000.0f) + 500.0f);
				
				SpriteNode enemy = null;
				
				//randomize
				if(fastSlow <= 0.4) {
					enemy = new SpriteNode("enemy_2", GameWorld.SCALE, GameObjectTypesEnum.ENEMY);
					FastSeekBehavior state = new FastSeekBehavior();
					state.setLockObject(player);				
					enemy.changeState(state);
					targetState.getDirectory().append(state.getName().hashCode(), state);
					GameMessageBus.dispatch(0, state.getName().hashCode(), randomDumb, GameMessageEnum.GO_DUMB);
				} else {
					enemy = new SpriteNode("enemy_1", GameWorld.SCALE, GameObjectTypesEnum.ENEMY);
					SlowSeekBehavior state = new SlowSeekBehavior();
					state.setLockObject(player);				
					enemy.changeState(state);
					targetState.getDirectory().append(state.getName().hashCode(), state);
					enemy.rotate(180.0f, new Vec3(0,0,1.0f));
					GameMessageBus.dispatch(0, state.getName().hashCode(), randomDumb + 500L, GameMessageEnum.GO_DUMB);
				}
				
				enemy.translateAnimation(new Vec3(dx, dy, 0)); //randomize
				enemy.setPhysics(new EnemyPhysics());
				targetState.getScene().addChild(enemy);
				targetState.getDirectory().append(enemy.getName().hashCode(), enemy);
				targetState.addPysicsObject(enemy);			
			}
		}
		
	}
	
	public void turnOn() {
		on = true;
	}
	
	public void turnOff() {
		on = false;
	}
}
