package com.solesurvivor.simplerender2_5.commands;

public enum CommandEnum {

	SHOW_MAIN_MENU(new ShowMainMenu()),
	REVERT_STATE(new RevertStateCommand()),
	STRAFE_LEFT(new StrafeLeft()),
	STRAFE_RIGHT(new StrafeRight()),
	WALK_FORWARD(new WalkForward()),
	WALK_BACKWARD(new WalkBackward()),
	TURN_LEFT(new TurnLeft()),
	TURN_RIGHT(new TurnRight()),
	FLY_UP(new FlyUp()),
	FLY_DOWN(new FlyDown()),
	ROTATE_VIEW(new RotateView());
	
	private Command mCommand;
	
	private CommandEnum(Command command) {
		this.mCommand = command;
	}
	
	public Command getCommand() {
		return mCommand;
	}
}
