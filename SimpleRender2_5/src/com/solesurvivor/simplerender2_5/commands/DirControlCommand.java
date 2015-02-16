package com.solesurvivor.simplerender2_5.commands;

import android.graphics.PointF;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.util.math.Vec2;
import com.solesurvivor.util.math.Vec3;

public class DirControlCommand implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = DirControlCommand.class.getSimpleName();
	
	private static final String CONTROL_EL_NAME = "dpad";
	
	protected float speed = 0.05f;
	protected Geometry control = null;
	protected float releaseSpeed = 0.5f;
	
	public void onStateChanged() {
		control = null;
		for(Geometry g : GameWorld.inst().getCurrentState().getUiElements()) {

			if(g.getName().equals(CONTROL_EL_NAME)) {
				control = g;
				break;
			}
		}
	}
	

	@Override
	public void execute(InputEvent event) {		
		Vec2 v = new Vec2(event.getViewCoords().x, event.getViewCoords().y);
		PointF center = event.getControlCenter();
		v.subtract(new Vec2(center.x, center.y));
		double angle = Math.atan2(v.getY(), v.getX());
		GameWorld.inst().getCurrentState().translateCurrentCamera(new Vec3((float)Math.cos(angle) * speed, 0.0f, (float)Math.sin(angle) * -speed));
		
		if(control != null) {
			float z = -5.0f;
			control.resetTransforms();
			control.translate(new Vec3(v.getX(), v.getY(), z));
		}
		
	}


	@Override
	public void release(InputEvent event) {
		if(control != null) {
			float z = -5.0f;
			control.resetTransforms();
			control.translate(new Vec3(0.0f, 0.0f, z));
			//XXX Should be control.beginAnimation();
		}
	}
}
