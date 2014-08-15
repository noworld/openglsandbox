package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.input.InputEvent;
import com.solesurvivor.simplerender2.rendering.RendererManager;

public class FewerElements implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = FewerElements.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		RendererManager.inst().getRenderer().mNumElements = RendererManager.inst().getRenderer().mNumElements - 3;
	}
}
