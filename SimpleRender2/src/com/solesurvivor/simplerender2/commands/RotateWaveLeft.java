package com.solesurvivor.simplerender2.commands;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2.game.GameWorld;
import com.solesurvivor.simplerender2.game.WaterWorldState;
import com.solesurvivor.simplerender2.input.InputEvent;
import com.solesurvivor.simplerender2.rendering.water.Wave;
import com.solesurvivor.simplerender2.scene.Water;
import com.solesurvivor.util.math.Vec3;

public class RotateWaveLeft implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = RotateWaveLeft.class.getSimpleName();
	
	protected float[] mRotMatrix = new float[16];
	protected float mAngle = -1.0f;
	
	public RotateWaveLeft() {
		init();
	}
	
	protected void init() {
		Matrix.setIdentityM(mRotMatrix, 0);
		Matrix.rotateM(mRotMatrix, 0, mAngle, 0.0f, 1.0f, 0.0f); //Y-up
	}

	@Override
	public void execute(InputEvent event) {
		if(GameWorld.inst().getCurrentState() instanceof WaterWorldState) {
			WaterWorldState state = (WaterWorldState)GameWorld.inst().getCurrentState();
			Water water = state.getLibrary().mWaters.get(0);
			if(water.getWaves().size() > 0) {
				Wave wave = water.getWaves().get(0);
				float[] waveDir = Vec3.toFloatArray(wave.getDirection());
				float[] d = new float[4];				
				System.arraycopy(waveDir, 0, d, 0, waveDir.length);				
				Matrix.multiplyMV(d, 0, mRotMatrix, 0, d, 0);
				wave.setDirection(Vec3.fromFloatArray(d));
			}
		}
	}
}
