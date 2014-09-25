package com.solesurvivor.simplerender2_5.commands;


public class WalkForwardCommand extends WalkBackwardCommand {

	@SuppressWarnings("unused")
	private static final String TAG = WalkForwardCommand.class.getSimpleName();
	
	public WalkForwardCommand() {
		this.mZDisp = -this.mZDisp;
	}

}
