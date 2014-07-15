package com.pimphand.simplerender2.scene;

import java.util.List;

import com.pimphand.simplerender2.fsm.State;

public abstract class GameObject {

	protected List<GameObject> mChildren;
	protected State<GameObject> mCurrentState;
	protected State<GameObject> mPreviousState;

	public boolean update() {

		for(GameObject ge : mChildren) {
			ge.update();
		}
		
		return true;
	}

	public boolean render() {

		for(GameObject ge : mChildren) {
			ge.render();
		}

		return true;
	}
	
	public boolean changeState(State<GameObject> state) {

		mCurrentState.exit(this);

		this.mPreviousState = mCurrentState;
		mCurrentState = state;

		mCurrentState.enter(this);

		return true;

	}
	
	public boolean revertState() {
		return changeState(mPreviousState);
	}
}
