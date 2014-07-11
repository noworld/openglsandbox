package com.solesurvivor.simplerender.ui;

import android.util.Log;

public class LoggingCommand implements Command {
	
	private static final String TAG = LoggingCommand.class.getSimpleName();

	@Override
	public void execute(Object[] data) {
		Log.d(TAG, String.format("%s Touched! (%s, %s)", data));
	}

}
