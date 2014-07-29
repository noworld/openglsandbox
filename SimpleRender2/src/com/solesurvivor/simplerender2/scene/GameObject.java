package com.solesurvivor.simplerender2.scene;

import java.util.List;

import com.solesurvivor.simplerender2.fsm.GameState;

public abstract class GameObject {

	protected List<GameObject> mChildren;
	protected GameState<GameObject> mCurrentState;
	protected GameState<GameObject> mPreviousState;
	protected boolean mDirty = true;
	
	public boolean isDirty() {
		return mDirty;
	}

	public void setDirty(boolean dirty) {
		this.mDirty = dirty;
	}

	public boolean update() {

		if(mChildren != null){
			for(GameObject ge : mChildren) {
				ge.update();
			}
		}
		
		return true;
	}

	public boolean render() {

		if(mChildren != null){
			for(GameObject ge : mChildren) {
				ge.render();
			}
		}

		return true;
	}
	
	public boolean changeState(GameState<GameObject> state) {

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
