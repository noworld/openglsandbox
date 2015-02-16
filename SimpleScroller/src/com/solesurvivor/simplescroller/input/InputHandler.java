package com.solesurvivor.simplescroller.input;

import com.solesurvivor.simplescroller.commands.Command;


public interface InputHandler {

	public boolean testInput(InputEvent event);
	public void fire();
	public void quiet();
	public void registerCommand(Command c);
	public void removeCommand(Command c);
	
}
