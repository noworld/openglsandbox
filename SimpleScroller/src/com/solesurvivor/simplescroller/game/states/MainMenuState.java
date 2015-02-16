package com.solesurvivor.simplescroller.game.states;

import com.solesurvivor.simplescroller.commands.CommandEnum;
import com.solesurvivor.simplescroller.input.BackButtonInputHandler;

public class MainMenuState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	public MainMenuState() {
		super();
		BackButtonInputHandler bbih = new BackButtonInputHandler();
		bbih.registerCommand(CommandEnum.PLAY_GAME.getCommand());
		inputHandlers.add(bbih);
	}
	
	@Override
	public void enter() {
		super.enter();
	}
	
	@Override
	public void execute() {
		super.execute();
	}
	
	@Override
	public void render() {		
		super.render();
	}
	
	@Override
	public void exit() {
		super.exit();
	}

}
