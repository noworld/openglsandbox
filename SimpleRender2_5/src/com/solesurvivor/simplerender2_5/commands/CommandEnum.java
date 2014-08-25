package com.solesurvivor.simplerender2_5.commands;

public enum CommandEnum {

	SHOW_MAIN_MENU(new ShowMainMenu()),
	REVERT_STATE(new RevertStateCommand());
	
	private Command mCommand;
	
	private CommandEnum(Command command) {
		this.mCommand = command;
	}
	
	public Command getCommand() {
		return mCommand;
	}
}
