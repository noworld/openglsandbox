package com.solesurvivor.pthirtyeight.commands;

public enum CommandEnum {

	SHOW_MAIN_MENU(new ChangeToMainMenu()),
	PLAY_GAME(new ChangeToPlay()),
	REVERT_STATE(new RevertStateCommand()),
	FIRE(new FireCommand()),
	DIR_CONTROL(new DirControlCommand()),
	CREATE_ENEMY(new CreateEnemyCommand());
	
	private Command mCommand;
	
	private CommandEnum(Command command) {
		this.mCommand = command;
	}
	
	public Command getCommand() {
		return mCommand;
	}
}
