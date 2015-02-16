package com.solesurvivor.simplescroller.scene;


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

		if(dirty || (parent != null && parent.isDirty())) {
			recalcMatrix();
			this.dirty = true;
		}
		
		if(currentState != null) {
			currentState.execute(this);
		}

		for(Node n : children) {
			n.update();
		}
		
		this.dirty = false;
	}

}
