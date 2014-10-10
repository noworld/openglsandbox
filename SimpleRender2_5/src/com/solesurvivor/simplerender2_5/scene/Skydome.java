package com.solesurvivor.simplerender2_5.scene;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;

public class Skydome extends Geometry implements Node {
	
	protected BaseRenderer ren = null;
	
	public Skydome(Geometry geo) {
		super(geo);
		ren = RendererManager.getRenderer();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		ren.drawSkydome(this);
	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}

}
