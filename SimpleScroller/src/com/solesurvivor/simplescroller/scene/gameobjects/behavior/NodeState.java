package com.solesurvivor.simplescroller.scene.gameobjects.behavior;

import com.solesurvivor.simplescroller.scene.StatefulNodeImpl;

public interface NodeState {

	public void enter(StatefulNodeImpl target);
	
	public void execute(StatefulNodeImpl target);
	
	public void exit(StatefulNodeImpl target);
	
}
