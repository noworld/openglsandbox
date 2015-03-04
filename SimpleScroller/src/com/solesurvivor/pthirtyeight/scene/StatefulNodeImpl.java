package com.solesurvivor.pthirtyeight.scene;

import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.NodeState;


public class StatefulNodeImpl extends NodeImpl {
	
	protected NodeState currentState;
	protected NodeState previousState;
	
	public NodeState getCurrentState() {
		return currentState;
	}
	
	public boolean changeState(NodeState state) {

		if(currentState != null) {
			currentState.exit(this);
			previousState = currentState;
		}
		
		currentState = state;
		
		currentState.enter(this);

		return true;
	}
	
	public boolean revertState() {
		return changeState(previousState);
	}
	
	@Override
	public void update() {

		if(currentState != null) {
			currentState.execute(this);
		}

		synchronized(this) {
			for(Node n : children) {
				n.update();
			}
		}
		
		if(dirty || (parent != null && parent.isDirty())) {
			this.dirty = false;
			recalcMatrix();			
		}
		
	}

}
