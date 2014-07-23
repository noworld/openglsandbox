package com.pimphand.simplerender2.commands;

public enum CommandEnum {

	SHOW_MAIN_MENU(new ShowMainMenu()),
	SHOW_WATER_WORLD(new ShowWaterWorld()),
	SHOW_MODEL_VIEWER(new ShowModelViewer()),
	REVERT_STATE(new RevertStateCommand());
	
	private Command mCommand;
	
	private CommandEnum(Command command) {
		this.mCommand = command;
	}
	
	public Command getCommand() {
		return mCommand;
	}
}
