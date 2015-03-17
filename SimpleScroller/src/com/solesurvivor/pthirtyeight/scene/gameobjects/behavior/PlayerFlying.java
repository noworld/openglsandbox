package com.solesurvivor.pthirtyeight.scene.gameobjects.behavior;

import android.graphics.Point;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessage;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageBus;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageEnum;
import com.solesurvivor.pthirtyeight.game.messaging.MessageReceiver;
import com.solesurvivor.pthirtyeight.game.states.GameState;
import com.solesurvivor.pthirtyeight.physics.BulletPhysics;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.pthirtyeight.scene.StatefulNodeImpl;
import com.solesurvivor.pthirtyeight.scene.gameobjects.GameObjectTypesEnum;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteManager;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec2;
import com.solesurvivor.util.math.Vec3;

public class PlayerFlying implements NodeState, MessageReceiver {
	
	public static final String NAME = "PlayerFlyingState";
	
	@SuppressWarnings("unused")
	private static final String TAG = PlayerFlying.class.getSimpleName();
	private static final float PIXELS_PER_SEC = 125;
	private static final float BUFFER = 20.0f;

	private Point view;
	private Vec2 dir = Vec2.createZeroVec2();
	private boolean fire = false;
	private GameState parentState;
	private BulletPhysics bulletPhysics;
	
	public PlayerFlying(GameState state) {
		parentState = state;
		bulletPhysics = new BulletPhysics();
	}

	@Override
	public void enter(StatefulNodeImpl target) {
		this.view = RendererManager.getViewport();
		parentState.getDirectory().append(NAME.hashCode(), this);
		((SpriteNode)target).setSprite(SpriteManager.getSprite("player_ac")); 
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		float deltaSec = ((float)GameWorld.inst().getDeltaT()) / 1000.0f;
		float deltaPx = deltaSec * PIXELS_PER_SEC;
		Vec3 trans = new Vec3(dir.getX() * deltaPx, dir.getY() * deltaPx, 0.0f);
		SpriteNode pa = (SpriteNode)target;
		Vec3 animTrans = pa.getAnimTransScale();		
		
//		if(Math.abs(animTrans.getX() + trans.getX()) >= (view.x / 2.0f) - BUFFER) {
//			trans.setX(trans.getX() - Math.copySign(BUFFER, animTrans.getX()));
//		}
//		
//		if(Math.abs(animTrans.getY() + trans.getY()) >= (view.y / 2.0f) - BUFFER) {
//			trans.setY(trans.getY() - Math.copySign(BUFFER, animTrans.getY()));
//		}
		
		pa.translateAnimation(trans);

		if(fire) {
			fire = false;
			SpriteNode oneBullet = new SpriteNode("one_bullet", GameWorld.SCALE, GameObjectTypesEnum.SELF_BULLET);
			oneBullet.changeState(new BulletTravel());
			oneBullet.setPhysics(bulletPhysics);
			Vec3 translation = pa.getAnimTrans().clone();
			oneBullet.translateAnimation(translation);
//			SSLog.d(TAG, "Bullet Start Point: %s", translation.prettyString());
			GameMessageBus.dispatch(0, parentState.getName().hashCode(), GameMessageEnum.FIRE, oneBullet);			
		}

	}

	@Override
	public void exit(StatefulNodeImpl target) {
		parentState.getDirectory().remove(NAME.hashCode());
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void receive(GameMessage message) {
		if(message != null) {
			 switch(message.getMessage()) {
			case STOP: dir = Vec2.createZeroVec2();
				break;
			case TRANSLATE: dir = ((Vec2)message.getData()).normalizeClone();
				break;
			case FIRE:
				fire = true;
				break;
			default:
				break;
			 }
		}
	}

}
