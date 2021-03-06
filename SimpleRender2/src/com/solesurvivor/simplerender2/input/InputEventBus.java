package com.solesurvivor.simplerender2.input;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



public class InputEventBus {
	
	@SuppressWarnings("unused")
	private static final String TAG = InputEventBus.class.getSimpleName();

	private static InputEventBus sInstance = new InputEventBus();
	
	private List<InputEvent> mEventList = Collections.synchronizedList(new LinkedList<InputEvent>());
	
	private InputEventBus() {

	}
	
	public static InputEventBus inst() {
		return sInstance;
	}
	
	public boolean add(InputEvent ie) {
		boolean result = false;
		synchronized(mEventList) {
			result = mEventList.add(ie);
		}
		return result;
	}
	
	public boolean removeAll(List<InputEvent> deadEvents) {
		boolean result = false;
		synchronized(mEventList) {
			result = mEventList.removeAll(deadEvents);
		}
		return result;
	}
	
	public void clear() {
		synchronized(mEventList) {
			mEventList.clear();
		}		
	}
	
	public void executeCommands(List<InputHandler> inputs) {
		synchronized(mEventList) {			
//			if(mEventList.size() > 0){Log.d(TAG, "***INPUT EVENT LIST FOLLOWS***");}
			for(InputEvent event : mEventList) {
//				if(mEventList.size() > 0){Log.d(TAG, String.format("%s at (%s,%s).", event.getEvent().toString(),
//						event.getCoords().x,
//						event.getCoords().y ));}
				
				for(InputHandler ih : inputs) {
//					Log.d(TAG, String.format("Bus testing input handler: %s", ih.getClass().getSimpleName()));
					if(ih.testInput(event)) break;
				}
			}
			
			mEventList.clear();
			
			for(InputHandler ih : inputs) {
//				Log.d(TAG, String.format("Bus firing input handler: %s", ih.getClass().getSimpleName()));
				ih.fire();
			}
		}
	}
	

}
