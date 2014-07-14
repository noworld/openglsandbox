package com.solesurvivor.simplerender.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

@SuppressLint("UseSparseArrays")
public class EventRecorder {

	private Map<Integer,String> mEvents = Collections.synchronizedMap(new HashMap<Integer,String>());
	private int mMaxEvents = 10000;
	private int mEventCtr = 0;

	private static EventRecorder mInstance = new EventRecorder();
	
	private EventRecorder() {
	}
	
	public static EventRecorder getInstance() {

		return mInstance;
	}

	public void record(String s) {
		synchronized(mEvents) {
			mEvents.put(mEventCtr++, s);
		}
		if(mEventCtr > mMaxEvents) mEventCtr = 0;
	}
	
	public  Map<Integer,String> getEvents() {
		return mEvents;
	}
	
	public int getCurrentIndex() {
		return mEventCtr;
	}
}
