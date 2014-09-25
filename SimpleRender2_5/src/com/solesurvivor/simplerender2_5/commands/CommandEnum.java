package com.solesurvivor.simplerender2_5.commands;

public enum CommandEnum {

	SHOW_MAIN_MENU(new ShowMainMenu()),
	REVERT_STATE(new RevertStateCommand()),
//	STRAFE_LEFT(new StrafeLeft()),
//	STRAFE_RIGHT(new StrafeRight()),
//	WALK_FORWARD(new WalkForward()),
//	WALK_BACKWARD(new WalkBackward()),
//	TURN_LEFT(new TurnLeft()),
//	TURN_RIGHT(new TurnRight()),
//	FLY_UP(new FlyUp()),
//	FLY_DOWN(new FlyDown()),
//	ROTATE_VIEW(new RotateView())
	STRAFE_LEFT(new StrafeLeftCommand()),
	STRAFE_RIGHT(new StrafeRightCommand()),
	WALK_FORWARD(new WalkForwardCommand()),
	WALK_BACKWARD(new WalkBackwardCommand()),
	TURN_LEFT(new TurnLeftCommand()),
	TURN_RIGHT(new TurnRightCommand()),
	FLY_UP(new FlyUpCommand()),
	FLY_DOWN(new FlyDownCommand()),
	ROTATE_VIEW(new RotateViewCommand())	
	;
	
	private Command mCommand;
	
	private CommandEnum(Command command) {
		this.mCommand = command;
	}
	
	public Command getCommand() {
		return mCommand;
	}
}
