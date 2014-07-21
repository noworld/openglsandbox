package com.pimphand.simplerender2.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.ui.commands.Command;

public class InputUiElement extends UiElement {

	protected InputArea mInputArea;
	protected List<Command> mCommands;
	
	public InputUiElement(Geometry geometry, InputArea inputArea) {
		super(geometry);
		this.mGeometry = geometry;
		this.mInputArea = inputArea;
		this.mCommands = new ArrayList<Command>();
	}
	
	public boolean input(InputEvent event) {
		if(mInputArea.isPressed(event.getCoords())) {
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
