package com.solesurvivor.simplerender.ui;

public class VibrateCommand implements Command {

	@Override
	public void execute(Object[] data) {
		TouchFeedback.instance().shortVib();
	}

}
