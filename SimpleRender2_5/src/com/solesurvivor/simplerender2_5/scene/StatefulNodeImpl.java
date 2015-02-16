package com.solesurvivor.simplerender2_5.scene;


public class StatefulNodeImpl extends NodeImpl {
	
	protected NodeState<NodeImpl> currentState;
	protected NodeState<NodeImpl> previousState;
	
	public NodeState<? extends NodeImpl> getCurrentState() {
		return currentState;
	}
	
	public boolean changeState(NodeState<NodeImpl> state) {

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

		if(mDirty || (parent != null && parent.isDirty())) {
			recalcMatrix();
			this.mDirty = true;
		}
		
		if(currentState != null) {
			currentState.execute(this);
		}

		for(Node n : children) {
			n.update();
		}
		
		this.mDirty = false;
	}

}
