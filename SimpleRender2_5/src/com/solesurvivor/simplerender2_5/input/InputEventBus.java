package com.solesurvivor.simplerender2_5.input;

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
			for(InputEvent event : mEventList) {
				
				for(InputHandler ih : inputs) {
					if(ih.testInput(event)) break; //TODO: Make some overlap but not others
				}
			}
			
			mEventList.clear();
			
			for(InputHandler ih : inputs) {
				ih.fire();
			}
		}
	}
	

}
