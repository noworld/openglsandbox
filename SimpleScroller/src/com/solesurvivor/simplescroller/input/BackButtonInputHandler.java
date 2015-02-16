package com.solesurvivor.simplescroller.input;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.simplescroller.commands.Command;

public class BackButtonInputHandler implements InputHandler {
	
	@SuppressWarnings("unused")
	private static final String TAG = BackButtonInputHandler.class.getSimpleName();

	protected boolean fire = false;
	protected InputEvent touch = null;
	protected List<Command> commands = new ArrayList<Command>();
	
	@Override
	public boolean testInput(InputEvent event) {
		if(event.getEvent().equals(InputEventEnum.BACK_BUTTON)) {
			touch = event;
			fire = true;
		}
		return fire;
	}

	@Override
	public void fire() {
		if(fire) {
			for(Command c : commands) {
				c.execute(touch);
			}
			
			fire = false;
		}
	}

	@Override
	public void registerCommand(Command c) {
		this.commands.add(c);
	}

	@Override
	public void removeCommand(Command c) {
		this.commands.remove(c);
	}

	@Override
	public void quiet() {
		fire = false;
		touch = null;
	}

}
