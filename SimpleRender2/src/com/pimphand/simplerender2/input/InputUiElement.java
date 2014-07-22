package com.pimphand.simplerender2.input;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.pimphand.simplerender2.commands.Command;
import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.scene.UiElement;

public class InputUiElement extends UiElement implements InputHandler {
	
	private static final String TAG = InputUiElement.class.getSimpleName();

	protected InputArea mInputArea;
	protected List<Command> mCommands;
	protected InputEvent mPreviousTouch;
	protected InputEvent mTouch;
	protected boolean mPressed;

	public InputUiElement(Geometry geometry, InputArea inputArea) {
		super(geometry);
		this.mGeometry = geometry;
		this.mInputArea = inputArea;
		this.mCommands = new ArrayList<Command>();
		quiet();
	}

	@Override
	public boolean testInput(InputEvent event) {
		
		boolean myEvent = mInputArea.isPressed(event.getCoords());

		if(myEvent) {
			mPressed = event.getEvent().equals(InputEventEnum.DOWN)
					|| event.getEvent().equals(InputEventEnum.MOVE_ON);
			
			mTouch = event;
		}

		return myEvent;
	}
	
	@Override
	public void fire() {
		//If the button is down
		if(mPressed) {
			Log.d(TAG, String.format("SCREEN button firing on %s at %s,%s", mTouch.getEvent().toString(), mTouch.getCoords().x, mTouch.getCoords().y));
			//Vibrate when changing state to pressed 
			if(mPreviousTouch == null
					|| mPreviousTouch.getEvent().equals(InputEventEnum.UP)
					|| mPreviousTouch.getEvent().equals(InputEventEnum.MOVE_OFF)) {
				TouchFeedback.touch();
			}
			
			for(Command c : mCommands) {
				c.execute(mTouch);
			}
		}
		
		mPreviousTouch = mTouch;
	}

	@Override
	public void registerCommand(Command c) {
		this.mCommands.add(c);
	}

	@Override
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

	@Override
	public void quiet() {
		this.mPreviousTouch = null;
		this.mTouch = null;
		this.mPressed = false;
	}

}
