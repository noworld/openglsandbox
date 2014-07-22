package com.pimphand.simplerender2.input;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.pimphand.simplerender2.ui.InputUiElement;


public class InputEventBus {
	
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
	
	public void executeCommands(List<InputUiElement> inputs) {
		synchronized(mEventList) {			
			if(mEventList.size() > 0){Log.d(TAG, "***INPUT EVENT LIST FOLLOWS***");}
			for(InputEvent event : mEventList) {
				if(mEventList.size() > 0){Log.d(TAG, String.format("%s at (%s,%s).", event.getEvent().toString(),
						event.getCoords().x,
						event.getCoords().y ));}
				
				for(InputUiElement iui : inputs) {
					if(iui.input(event)) break;
				}
			}
			
			mEventList.clear();
			
			for(InputUiElement iui : inputs) {
				iui.fire();
			}
		}
	}
	

}
