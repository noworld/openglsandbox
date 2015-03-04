package com.solesurvivor.pthirtyeight.scene.gameobjects.behavior;

import com.solesurvivor.pthirtyeight.scene.StatefulNodeImpl;

public interface NodeState {

	public void enter(StatefulNodeImpl target);
	
	public void execute(StatefulNodeImpl target);
	
	public void exit(StatefulNodeImpl target);
	
}
