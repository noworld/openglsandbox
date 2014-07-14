package com.solesurvivor.simplerender.ui;

import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.RendererManager;

public class RotateLightPosCommand implements Command {

	@Override
	public void execute(Object[] data) {
		((BetterUiGLTextureRenderer)RendererManager.getInstance().getRenderer()).mAccumRotation += 1.0f;
		if(((BetterUiGLTextureRenderer)RendererManager.getInstance().getRenderer()).mAccumRotation >= 360.0f) {
			((BetterUiGLTextureRenderer)RendererManager.getInstance().getRenderer()).mAccumRotation = 0.0f;
		}
	}

}
