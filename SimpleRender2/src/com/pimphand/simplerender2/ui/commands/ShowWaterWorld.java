package com.pimphand.simplerender2.ui.commands;

import com.pimphand.simplerender2.fsm.WaterWorldState;
import com.pimphand.simplerender2.input.InputEvent;
import com.pimphand.simplerender2.scene.GameWorld;

public class ShowWaterWorld implements Command {

	@Override
	public void execute(InputEvent event) {
		GameWorld.inst().changeState(new WaterWorldState());
	}

}
