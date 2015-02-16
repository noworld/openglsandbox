package com.solesurvivor.simplescroller.commands;

import com.solesurvivor.simplescroller.input.InputEvent;


public interface Command {

	public void execute(InputEvent event);
	public void release(InputEvent event);
	public void onStateChanged();
	
}
