package com.solesurvivor.simplerender2.commands;

public enum CommandEnum {

	SHOW_MAIN_MENU(new ShowMainMenu()),
	SHOW_WATER_WORLD(new ShowWaterWorld()),
	SHOW_MODEL_VIEWER(new ShowModelViewer()),
	REVERT_STATE(new RevertStateCommand()),
	WALK_FORWARD(new WalkForward()),
	WALK_BACKWARD(new WalkBackward()),
	TURN_LEFT(new TurnLeft()),
	TURN_RIGHT(new TurnRight()),
	JUMP(new Jump()),
	INCREASE_NEAR(new IncreaseNear()),
	DECREASE_NEAR(new DecreaseNear()),
	INCREASE_FAR(new IncreaseFar()),
	DECREASE_FAR(new DecreaseFar()),
	INCREASE_ASPECT(new IncreaseAspect()),
	DECREASE_ASPECT(new DecreaseAspect()),
	ROT_WAVE_LEFT(new RotateWaveLeft()),
	ROT_WAVE_RIGHT(new RotateWaveRight());
	
	private Command mCommand;
	
	private CommandEnum(Command command) {
		this.mCommand = command;
	}
	
	public Command getCommand() {
		return mCommand;
	}
}
