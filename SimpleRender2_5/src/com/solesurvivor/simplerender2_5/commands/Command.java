package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.input.InputEvent;


public interface Command {

	public void execute(InputEvent event);
	public void release(InputEvent event);
	public void onStateChanged();
	
}
