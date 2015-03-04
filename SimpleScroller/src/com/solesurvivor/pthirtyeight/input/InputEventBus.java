package com.solesurvivor.pthirtyeight.input;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



public class InputEventBus {
	
	@SuppressWarnings("unused")
	private static final String TAG = InputEventBus.class.getSimpleName();

	private static InputEventBus instance = new InputEventBus();
	
	private List<InputEvent> eventList = Collections.synchronizedList(new LinkedList<InputEvent>());
	
	private InputEventBus() {

	}
	
	public static InputEventBus inst() {
		return instance;
	}
	
	public boolean add(InputEvent ie) {
		boolean result = false;
		synchronized(eventList) {
			result = eventList.add(ie);
		}
		return result;
	}
	
	public boolean removeAll(List<InputEvent> deadEvents) {
		boolean result = false;
		synchronized(eventList) {
			result = eventList.removeAll(deadEvents);
		}
		return result;
	}
	
	public void clear() {
		synchronized(eventList) {
			eventList.clear();
		}		
	}
	
	public void executeCommands(List<InputHandler> inputs) {
		synchronized(eventList) {			
			for(InputEvent event : eventList) {
				
				for(InputHandler ih : inputs) {
					if(ih.testInput(event)) break; //TODO: Make some overlap but not others
				}
			}
			
			eventList.clear();
			
			for(InputHandler ih : inputs) {
				ih.fire();
			}
		}
	}
	

}
