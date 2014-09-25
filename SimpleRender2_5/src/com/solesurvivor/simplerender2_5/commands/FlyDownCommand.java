package com.solesurvivor.simplerender2_5.commands;


public class FlyDownCommand extends FlyUpCommand {

	@SuppressWarnings("unused")
	private static final String TAG = FlyDownCommand.class.getSimpleName();
	
	public FlyDownCommand() {
		this.mYDisp= -this.mYDisp;
	}

}
