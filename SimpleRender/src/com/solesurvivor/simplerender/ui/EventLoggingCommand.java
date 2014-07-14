package com.solesurvivor.simplerender.ui;

import java.util.Map;

import android.util.Log;

import com.solesurvivor.simplerender.util.EventRecorder;

public class EventLoggingCommand implements Command {
	
	private static final String TAG = EventLoggingCommand.class.getSimpleName();

	@Override
	public void execute(Object[] data) {
		
		Map<Integer,String> events = EventRecorder.getInstance().getEvents();
		synchronized(events) {
			Log.d(TAG, "--- * BEGIN INPUT EVENT LOG * ---");
			Log.d(TAG, String.format("CURRENT INDEX: %s", EventRecorder.getInstance().getCurrentIndex()));
			for(String s : events.values()) {
				Log.d(TAG, s);
			}
			Log.d(TAG, String.format("CURRENT INDEX: %s", EventRecorder.getInstance().getCurrentIndex()));
			Log.d(TAG, "--- * END INPUT EVENT LOG * ---");
		}
	}

}
