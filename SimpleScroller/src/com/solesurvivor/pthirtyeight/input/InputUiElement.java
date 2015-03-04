package com.solesurvivor.pthirtyeight.input;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.pthirtyeight.commands.Command;
import com.solesurvivor.pthirtyeight.scene.Geometry;

public class InputUiElement extends UiElement implements InputHandler {
	
	@SuppressWarnings("unused")
	private static final String TAG = InputUiElement.class.getSimpleName();

	protected String name;
	protected InputArea inputArea;
	protected List<Command> commands;
	protected InputEvent previousTouch;
	protected InputEvent touch;
	protected boolean pressed;
	protected boolean released;

	public InputUiElement(String name, Geometry geometry, InputArea inputArea) {
		super(geometry);
		this.name = name;
		this.geometry = geometry;
		this.inputArea = inputArea;
		this.commands = new ArrayList<Command>();
		quiet();
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean testInput(InputEvent event) {
		
//		PointF view = UiUtil.screenToViewCoords(event.getCoords());
		boolean myEvent = inputArea.isPressed(event.getViewCoords());

		//XXX DEBUG STICKING INPUT
//		PointF screen = UiUtil.viewToScreenCoords(event.getCoords());
		
		if(myEvent) {
			
			released = pressed && (event.getEvent().equals(InputEventEnum.UP)
					|| event.getEvent().equals(InputEventEnum.MOVE_OFF));
			
			pressed = event.getEvent().equals(InputEventEnum.DOWN)
					|| event.getEvent().equals(InputEventEnum.MOVE_ON);
			
			touch = event;
			
			//XXX DEBUG STICKING INPUT
//			Log.d(TAG, String.format("%s testest POS for %s,%s", mName, screen.x, screen.y));
		} else {
			//XXX DEBUG STICKING INPUT
//			Log.d(TAG, String.format("%s testest NEG for %s,%s", mName, screen.x, screen.y));
		}

		return myEvent;
	}
	
	@Override
	public void fire() {
		//If the button is down
		if(pressed) {
//			Log.d(TAG, String.format("SCREEN button firing on %s at %s,%s", mTouch.getEvent().toString(), mTouch.getCoords().x, mTouch.getCoords().y));
			//Vibrate when changing state to pressed 
//			if(mPreviousTouch == null
//					|| mPreviousTouch.getEvent().equals(InputEventEnum.UP)
//					|| mPreviousTouch.getEvent().equals(InputEventEnum.MOVE_OFF)) {
//				TouchFeedback.touch();
//			}
			
			for(Command c : commands) {
				touch.setControlCenter(inputArea.getInputCenter());
				c.execute(touch);
			}
			
		} else if(released) {
			for(Command c : commands) {
				touch.setControlCenter(inputArea.getInputCenter());
				c.release(touch);
			}
		}
		
		previousTouch = touch;
	}

	@Override
	public void registerCommand(Command c) {
		this.commands.add(c);
	}

	@Override
	public void removeCommand(Command c) {
		this.commands.remove(c);
	}

	@Override
	public void scale(float x, float y, float z) {
		this.inputArea.scale(x, y, z);
		super.scale(x, y, z);
	}

	@Override
	public void translate(float x, float y, float z) {
		this.inputArea.translate(x, y, z);
		super.translate(x, y, z);
	}

	@Override
	public void reset() {
		this.inputArea.reset();
		super.reset();
	}

	@Override
	public void quiet() {
		this.previousTouch = null;
		this.touch = null;
		this.pressed = false;
	}

}
