package com.pimphand.simplerender2.input;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.pimphand.simplerender2.commands.Command;

public class BackButtonInputHandler implements InputHandler {
	
	private static final String TAG = BackButtonInputHandler.class.getSimpleName();

	protected boolean mFire = false;
	protected InputEvent mTouch = null;
	protected List<Command> mCommands = new ArrayList<Command>();
	
	@Override
	public boolean testInput(InputEvent event) {
		if(event.getEvent().equals(InputEventEnum.BACK_BUTTON)) {
			mTouch = event;
			mFire = true;
		}
		return mFire;
	}

	@Override
	public void fire() {
		if(mFire) {
			Log.d(TAG, String.format("BACK button firing on %s at %s,%s", mTouch.getEvent().toString(), mTouch.getCoords().x, mTouch.getCoords().y));
			for(Command c : mCommands) {
				c.execute(mTouch);
			}
			
			mFire = false;
		}
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
	public void quiet() {
		mFire = false;
		mTouch = null;
	}

}
