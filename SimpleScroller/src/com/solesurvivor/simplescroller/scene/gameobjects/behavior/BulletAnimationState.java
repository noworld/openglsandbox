package com.solesurvivor.simplescroller.scene.gameobjects.behavior;

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.simplescroller.scene.StatefulNodeImpl;
import com.solesurvivor.simplescroller.scene.gameobjects.SpriteNode;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class BulletAnimationState implements NodeState {
	
//	@SuppressWarnings("unused")
	private static final String TAG = BulletAnimationState.class.getSimpleName();
	private static final float SPEED = 400;
	private static final float MAX_D = 550;	
//	private static final long TTL = 1250;
	
	private long startTime;	
	
	@Override
	public void enter(StatefulNodeImpl target) {
		this.startTime = GameWorld.inst().getGameT();
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		SpriteNode s = (SpriteNode)target;
		
		double deltaSec = ((double)GameWorld.inst().getDeltaT()) / 1000.0;
		double deltaPx = (deltaSec * (SPEED + s.getSpeed()));
		Vec3 trans = Vec3.createZeroVec3();
		trans.setY((float)deltaPx);
		
//		if(GameWorld.inst().getGameT() - startTime > TTL) {
		if(s.getAnimTrans().getY() > MAX_D) {
			s.setAlive(false);
//			SSLog.d(TAG, "Bullet died.");
		} else {
			s.translateAnimation(trans);
		}		
	}

	@Override
	public void exit(StatefulNodeImpl target) {
		
	}



}
