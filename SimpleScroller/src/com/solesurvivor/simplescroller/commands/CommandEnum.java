package com.solesurvivor.simplescroller.commands;

public enum CommandEnum {

	SHOW_MAIN_MENU(new ChangeToMainMenu()),
	PLAY_GAME(new ChangeToPlay()),
	REVERT_STATE(new RevertStateCommand());
	
	private Command mCommand;
	
	private CommandEnum(Command command) {
		this.mCommand = command;
	}
	
	public Command getCommand() {
		return mCommand;
	}
}
