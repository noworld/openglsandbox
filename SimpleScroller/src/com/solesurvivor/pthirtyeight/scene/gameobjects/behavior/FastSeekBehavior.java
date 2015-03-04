package com.solesurvivor.pthirtyeight.scene.gameobjects.behavior;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessage;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageEnum;
import com.solesurvivor.pthirtyeight.game.messaging.MessageReceiver;
import com.solesurvivor.pthirtyeight.scene.Node;
import com.solesurvivor.pthirtyeight.scene.StatefulNodeImpl;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.util.math.Vec3;

public class FastSeekBehavior implements NodeState, MessageReceiver {
	
	@SuppressWarnings("unused")
	private static final String TAG = FastSeekBehavior.class.getSimpleName();
	private static final float MAX_SPEED = 400;
	private static final long TTL = 10*1000;
	private static final double ACCEL = 2.0;
	
	private String name;
	private Vec3 velocity;
	private long startTime = -1L;	
	private Node lockObject = null;
	
	@Override
	public void enter(StatefulNodeImpl target) {
		this.name = java.util.UUID.randomUUID().toString();
		this.startTime = GameWorld.inst().getGameT();
		velocity = Vec3.createZeroVec3();
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		
		if(startTime > 0) {
			SpriteNode s = (SpriteNode)target;	
			
//			float deltaSec = ((float)GameWorld.inst().getDeltaT()) / 1000.0f;
//			float speed = deltaSec * MAX_SPEED;

			if(lockObject == null) {
				//Default behavior?
			} else {
				Vec3 myPos = Vec3.fromFloatArray(new float[]{target.getTransMatrix()[12],target.getTransMatrix()[13],target.getTransMatrix()[14]});
				Vec3 otherPos = Vec3.fromFloatArray(new float[]{lockObject.getTransMatrix()[12],lockObject.getTransMatrix()[13],lockObject.getTransMatrix()[1]});
				Vec3 dist = otherPos.clone();
				dist.subtract(myPos);
				float distance = dist.getMag();
				Vec3 desiredVelocity = Vec3.createZeroVec3();
				double speed = 0.0;
				
				if(distance > 0.0) {
					speed = Math.min(MAX_SPEED, distance * ACCEL);
					desiredVelocity = calcDesiredVelocity(myPos, otherPos, (float)speed/distance);
					desiredVelocity.subtract(velocity);
				}

				velocity.add(desiredVelocity);
			}

			s.translateAnimation(velocity);

			if(startTime + TTL < GameWorld.inst().getGameT()) {
				target.setAlive(false);
			}
		}
	}

	private Vec3 calcDesiredVelocity(Vec3 myPos, Vec3 otherPos, float speed) {
		Vec3 desiredVelocity = otherPos.clone();
		desiredVelocity.subtract(myPos);		
		desiredVelocity.normalize();
		desiredVelocity.scale(speed);
		return desiredVelocity;
	}

	@Override
	public void exit(StatefulNodeImpl target) {
		
	}
	
	public void setLockObject(Node lockObject) {
		this.lockObject = lockObject;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void receive(GameMessage message) {
		if(message.getMessage().equals(GameMessageEnum.GO_DUMB)) {
			this.lockObject = null;
		}
	}

}
