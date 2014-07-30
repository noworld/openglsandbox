package com.solesurvivor.simplerender2.commands;

import android.os.SystemClock;

import com.solesurvivor.simplerender2.fsm.GameState;
import com.solesurvivor.simplerender2.game.GameWorld;
import com.solesurvivor.simplerender2.game.WaterWorldState;
import com.solesurvivor.simplerender2.input.InputEvent;

public class SwitchWaveTex implements Command {
	
	//Result: 1 is the way to go
	private String[] mTextures = {"wavemapn1","wavemapn2"};
	private int index = 0;	
	private long pressT =  SystemClock.uptimeMillis();

	@SuppressWarnings("unused")
	private static final String TAG = SwitchWaveTex.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {		
		GameState<GameWorld> state = GameWorld.inst().getCurrentState();
		if(state instanceof WaterWorldState) {			
			WaterWorldState wws = (WaterWorldState)state;
			long tempT = SystemClock.uptimeMillis();
			if((tempT - pressT) > 500) { //twice per second max
				pressT = tempT;
				if(index == 0) {
					index = 1;
				} else {
					index = 0;
				}
				wws.setWaveTex(mTextures[index]);
			}
		}
	}
}
