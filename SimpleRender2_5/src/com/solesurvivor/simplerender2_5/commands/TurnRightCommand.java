package com.solesurvivor.simplerender2_5.commands;


public class TurnRightCommand extends TurnLeftCommand {

	@SuppressWarnings("unused")
	private static final String TAG = TurnRightCommand.class.getSimpleName();
	
	public TurnRightCommand() {
		this.mRot = -this.mRot;
	}

}
