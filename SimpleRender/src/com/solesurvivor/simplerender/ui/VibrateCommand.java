package com.solesurvivor.simplerender.ui;

public class VibrateCommand implements Command {
	
	private static final long[] VIB_PATTERN = {10, 50};

	@Override
	public void execute(Object[] data) {
		TouchFeedback.instance().vibPattern(VIB_PATTERN);
	}
}
