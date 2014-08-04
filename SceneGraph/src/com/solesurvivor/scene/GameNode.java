package com.solesurvivor.scene;

import com.solesurvivor.drawing.GameNodeRenderer;

public abstract class GameNode {

	public String mName;
	public String mAsset;
	public GameNode[] mChildren;
	
	public void init() {
		
	}
	
	public void update() {
		if(mChildren == null) return;
		for(GameNode so : mChildren) {
			so.update();
		}
	}
	
	public void draw(GameNodeRenderer gnr) {
		gnr.renderNode(this);
		if(mChildren == null) return;
		for(GameNode so : mChildren) {
			so.draw(gnr);
		}
	}
}
