package com.solesurvivor.simplerender2_5.input;

import com.solesurvivor.simplerender2_5.commands.Command;


public interface InputHandler {

	public boolean testInput(InputEvent event);
	public void fire();
	public void quiet();
	public void registerCommand(Command c);
	public void removeCommand(Command c);
	
}
