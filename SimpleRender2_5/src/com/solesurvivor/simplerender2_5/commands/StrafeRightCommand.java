package com.solesurvivor.simplerender2_5.commands;


public class StrafeRightCommand extends StrafeLeftCommand {

	@SuppressWarnings("unused")
	private static final String TAG = StrafeRightCommand.class.getSimpleName();
	
	public StrafeRightCommand() {
		this.mXDisp = -this.mXDisp;
	}

}
