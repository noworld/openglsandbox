package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;

public class RotateViewCommand implements Command {
	
	protected static final float DELTA_H = 0.5f;

	@Override
	public void execute(InputEvent event) {
		Camera cam = RendererManager.getRenderer().getCurrentCamera();
		float[] eye = cam.getEyePos();
//		float[] look = cam.getLookVector();
		cam.setEyePos(new float[]{eye[0], eye[1]+DELTA_H, eye[2]});
//		cam.setLookVector(new float[]{0.0f, 2.0f, -1.0f});
	}

}
