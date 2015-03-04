package com.solesurvivor.pthirtyeight.commands;

import com.solesurvivor.pthirtyeight.input.InputEvent;


public interface Command {

	public void execute(InputEvent event);
	public void release(InputEvent event);
	public void onStateChanged();
	
}
