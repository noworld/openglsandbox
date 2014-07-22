package com.pimphand.simplerender2.ui;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.pimphand.simplerender2.input.InputEvent;
import com.pimphand.simplerender2.input.InputEventEnum;
import com.pimphand.simplerender2.input.TouchFeedback;
import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.ui.commands.Command;

public class InputUiElement extends UiElement {
	
	private static final String TAG = InputUiElement.class.getSimpleName();

	protected InputArea mInputArea;
	protected List<Command> mCommands;
	protected InputEventEnum mPreviousTouch;
	protected InputEventEnum mTouch;
	protected boolean mPressed;

	public InputUiElement(Geometry geometry, InputArea inputArea) {
		super(geometry);
		this.mGeometry = geometry;
		this.mInputArea = inputArea;
		this.mCommands = new ArrayList<Command>();
		this.mPreviousTouch = InputEventEnum.UP;
		this.mTouch = InputEventEnum.UP;
		this.mPressed = false;
	}

	public boolean input(InputEvent event) {
		
		boolean myEvent = mInputArea.isPressed(event.getCoords());

		if(myEvent) {
			mPressed = event.getEvent().equals(InputEventEnum.DOWN)
					|| event.getEvent().equals(InputEventEnum.MOVE_ON);
			
			mTouch = event.getEvent();
		}

		return myEvent;
	}
	
	public void fire() {
		//If the button is down
		if(mPressed) {
			
			//Vibrate when changing state to pressed 
//			Log.d(TAG, String.format("Firing command on event %s -> %s.", mPreviousTouch.toString(), mTouch.toString()));
			if(mPreviousTouch.equals(InputEventEnum.UP)
					|| mPreviousTouch.equals(InputEventEnum.MOVE_OFF)) {
//				Log.d(TAG, String.format("Vibrating on event %s -> %s.", mPreviousTouch.toString(), mTouch.toString()));
				TouchFeedback.touch();
			}
			
			for(Command c : mCommands) {
				c.execute(null);
			}
		}
		
		mPreviousTouch = mTouch;
	}

	public void registerCommand(Command c) {
		this.mCommands.add(c);
	}

	public void removeCommand(Command c) {
		this.mCommands.remove(c);
	}

	@Override
	public void scale(float x, float y, float z) {
		this.mInputArea.scale(x, y, z);
		super.scale(x, y, z);
	}

	@Override
	public void translate(float x, float y, float z) {
		this.mInputArea.translate(x, y, z);
		super.translate(x, y, z);
	}

	@Override
	public void reset() {
		this.mInputArea.reset();
		super.reset();
	}

}
