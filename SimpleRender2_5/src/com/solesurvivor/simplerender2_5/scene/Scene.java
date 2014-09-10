package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;


public class Scene implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = Scene.class.getSimpleName();
	List<Node> mChildren = new ArrayList<Node>();

	@Override
	public void update() {
		for(Node n : mChildren) {
			n.update();
		}
	}

	@Override
	public void render() {
		for(Node n : mChildren) {
			n.render();
		}
	}

	@Override
	public void addChild(Node n) {
		mChildren.add(n);
	}
	
	
}
