package com.solesurvivor.simplescroller.scene.gameobjects.behavior;

import android.graphics.Point;

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.simplescroller.game.messaging.GameMessage;
import com.solesurvivor.simplescroller.game.messaging.GameMessageBus;
import com.solesurvivor.simplescroller.game.messaging.GameMessageEnum;
import com.solesurvivor.simplescroller.game.messaging.MessageReceiver;
import com.solesurvivor.simplescroller.game.states.GameState;
import com.solesurvivor.simplescroller.rendering.RendererManager;
import com.solesurvivor.simplescroller.scene.StatefulNodeImpl;
import com.solesurvivor.simplescroller.scene.gameobjects.SpriteNode;
import com.solesurvivor.util.math.Vec2;
import com.solesurvivor.util.math.Vec3;

public class PlayerFlyingState implements NodeState, MessageReceiver {
	
	public static final String NAME = "PlayerFlyingState";
	
	@SuppressWarnings("unused")
	private static final String TAG = PlayerFlyingState.class.getSimpleName();
	private static final float PIXELS_PER_SEC = 125;
	private static final float BUFFER = 10.0f;

	private Point view;
	private Vec2 dir = Vec2.createZeroVec2();
	private boolean fire = false;
	private GameState parentState;
	private BulletAnimationState bas;
	
	public PlayerFlyingState(GameState state) {
		parentState = state;
		bas = new BulletAnimationState();
	}

	@Override
	public void enter(StatefulNodeImpl target) {
		this.view = RendererManager.getViewport();
		parentState.getDirectory().append(NAME.hashCode(), this);
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		float deltaSec = ((float)GameWorld.inst().getDeltaT()) / 1000.0f;
		float deltaPx = deltaSec * PIXELS_PER_SEC;
		Vec3 trans = new Vec3(dir.getX() * deltaPx, dir.getY() * deltaPx, 0.0f);
		SpriteNode pa = (SpriteNode)target;
		Vec3 animTrans = pa.getAnimTransScale();		
		
		if(Math.abs(animTrans.getX() + trans.getX()) >= (view.x / 2.0f) - BUFFER) {
			trans.setX(trans.getX() - Math.copySign(BUFFER, animTrans.getX()));
		}
		
		if(Math.abs(animTrans.getY() + trans.getY()) >= (view.y / 2.0f) - BUFFER) {
			trans.setY(trans.getY() - Math.copySign(BUFFER, animTrans.getY()));
		}
		
		pa.translateAnimation(trans);		

		if(fire) {
			fire = false;
			SpriteNode oneBullet = new SpriteNode("one_bullet", 2.0f);
//			float speed = PIXELS_PER_SEC *  dir.getY();		
//			Math.copySign(speed, dir.getY());
//			SSLog.d(TAG, "Bullet speed is: %s", speed);
//			oneBullet.setSpeed(speed);
			oneBullet.changeState(bas);
			oneBullet.translateAnimationScale(pa.getAnimTrans());
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
			default:
				break;
			 
			 }
		}
		
		if(message.getMessage().equals(GameMessageEnum.FIRE)) {
			fire = true;
		}
	}

}
