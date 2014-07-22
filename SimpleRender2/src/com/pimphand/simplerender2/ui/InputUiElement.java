package com.pimphand.simplerender2.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

import com.pimphand.simplerender2.input.InputEvent;
import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.ui.commands.Command;

public class InputUiElement extends UiElement {

	protected InputArea mInputArea;
	protected List<Command> mCommands;
	protected boolean mPressed;

	public InputUiElement(Geometry geometry, InputArea inputArea) {
		super(geometry);
		this.mGeometry = geometry;
		this.mInputArea = inputArea;
		this.mCommands = new ArrayList<Command>();
	}

	public boolean input(InputEvent event) {

		if(!mPressed && event.getEvent().equals(InputEventEnum.DOWN)) {
			//FOR NEW DOWN EVENTS
			if(mInputArea.isPressed(event.getCoords())) {
				//If the user pressed this input area
				mPressed = true;
				//On Press events would go here
			}
		} else if(mPressed && event.getEvent().equals(InputEventEnum.MOVE)) {
			//FOR MOVE EVENTS AFTER DOWN
			//Don't really need to do anything. The button will still be down
		} else if(mPressed && event.getEvent().equals(InputEventEnum.UP)) {
			//FOR UP EVENTS AFTER DOWN
			if(mInputArea.isPressed(event.getCoords())) {
				//If the user released this input area
				mPressed = false;
			}
		}

		//If the button is down
		if(mPressed) {
			for(Command c : mCommands) {
				c.execute(event);
			}
		}

		return true;
	}

	public void registerCommand(Command c) {
		this.mCommands.add(c);
	}

	public void removeCommand(Command c) {
		this.mCommands.remove(c);
	}

	@Override
	public void setScale(PointF scale) {
		super.setScale(scale);
		this.mInputArea.setScale(scale);
	}
}
