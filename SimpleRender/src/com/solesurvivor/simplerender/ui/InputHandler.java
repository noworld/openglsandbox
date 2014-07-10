package com.solesurvivor.simplerender.ui;

public class InputHandler {

	public Command mCommand;
	public InputArea mInputArea;
	
	public void update(int x, int y) {
		if(mInputArea.isPressed(x, y)) {
			mCommand.execute();
		}
	}
	
}
