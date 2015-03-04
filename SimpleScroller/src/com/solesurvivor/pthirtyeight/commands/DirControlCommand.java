package com.solesurvivor.pthirtyeight.commands;

import android.graphics.PointF;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessage;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageBus;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageEnum;
import com.solesurvivor.pthirtyeight.input.InputEvent;
import com.solesurvivor.pthirtyeight.scene.Geometry;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.PlayerFlying;
import com.solesurvivor.util.math.Vec2;
import com.solesurvivor.util.math.Vec3;

public class DirControlCommand implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = DirControlCommand.class.getSimpleName();	
	private static final String CONTROL_EL_NAME = "dpad";
	private static final int RELEASE_DELAY = 30;
	
	protected float speed = 0.05f;
	protected Geometry control = null;
	protected float releaseSpeed = 0.5f;
	protected GameMessage lastReleaseMessage;
	protected int playerObject = 0;
	
	public void onStateChanged() {
		for(Geometry g : GameWorld.inst().getCurrentState().getUiElements()) {
			if(g.getName().equals(CONTROL_EL_NAME)) {
				control = g;
				break;
			}
		}
		
		playerObject = PlayerFlying.NAME.hashCode();
	}
	

	@Override
	public void execute(InputEvent event) {		
		Vec2 v = new Vec2(event.getViewCoords().x, event.getViewCoords().y);
		PointF center = event.getControlCenter();
		v.subtract(new Vec2(center.x, center.y));
		
		if(control != null) {
			if(lastReleaseMessage != null) {
				GameMessageBus.cancel(lastReleaseMessage);
				lastReleaseMessage = null;
			}
			
			Vec3 trans = new Vec3(v.getX(), v.getY(), -5.0f);
			control.resetTransforms();
			control.translate(trans);
			
			GameMessageBus.dispatch(0, playerObject, GameMessageEnum.TRANSLATE, (Vec2)trans);
		}
		
	}


	@Override
	public void release(InputEvent event) {
		if(control != null) {
			lastReleaseMessage = GameMessageBus.dispatch(this.hashCode(), control.getName().hashCode(), RELEASE_DELAY, GameMessageEnum.RESET_TRANSFORMS, new Vec3(0.0f, 0.0f, -5.0f));
			GameMessageBus.dispatch(0, playerObject, GameMessageEnum.STOP);
		}
	}
}
