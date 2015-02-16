package com.solesurvivor.simplerender2_5.scene;

public interface NodeState<T extends NodeImpl> {

	public void enter(T target);
	
	public void execute(T target);
	
	public void exit(T target);
	
}
