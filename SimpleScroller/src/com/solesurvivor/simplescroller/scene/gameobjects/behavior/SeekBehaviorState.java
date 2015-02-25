package com.solesurvivor.simplescroller.scene.gameobjects.behavior;

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.simplescroller.scene.Node;
import com.solesurvivor.simplescroller.scene.StatefulNodeImpl;
import com.solesurvivor.simplescroller.scene.gameobjects.SpriteNode;
import com.solesurvivor.util.math.Vec2;
import com.solesurvivor.util.math.Vec3;

public class SeekBehaviorState implements NodeState {
	
//	@SuppressWarnings("unused")
	private static final String TAG = SeekBehaviorState.class.getSimpleName();
	private static final float SPEED = 400;
	private static final long TTL = 10*1000;

	private Vec2 velocity;
	private long startTime;	
	private Node lockObject = null;
	
	@Override
	public void enter(StatefulNodeImpl target) {
		this.startTime = GameWorld.inst().getGameT();
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		SpriteNode s = (SpriteNode)target;
		
		Vec3 vehPos = s.getAnimTrans();
		Vec3 trans = Vec3.createZeroVec3();
		
		if(lockObject == null) {
			
		} else {
			
		}
		
		s.translateAnimation(trans);
		
		if(startTime + TTL < GameWorld.inst().getGameT()) {
			target.setAlive(false);
		}
	}

	@Override
	public void exit(StatefulNodeImpl target) {
		
	}
	
	public void setLockObject(Node lockObject) {
		this.lockObject = lockObject;
	}

}
