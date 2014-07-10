package com.solesurvivor.simplerender.ui;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.simplerender.Geometry;
import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.RendererManager;

public class UiManager {
	
	private static final String INPUTAREA_SUFFIX = "_inputarea";
	
	private List<InputUiElement> mInputs = new ArrayList<InputUiElement>();
	private List<DisplayUiElement> mDisplays = new ArrayList<DisplayUiElement>();
	
	public boolean mDrawInputAreas = true;

	public void loadUi(List<Geometry> geos) {
		
		for(Geometry geo : geos) {
			
			if(geo.mName.contains(INPUTAREA_SUFFIX)) {
				//If the suffix is included
				//then we are dealing with an input area
				InputUiElement iuie = new InputUiElement();
				iuie.mGeo = geo;
				mInputs.add(iuie);				
			} else {
				DisplayUiElement duie = new DisplayUiElement();
				duie.mGeo = geo;
				mDisplays.add(duie);
			}
		}
	}
	
	public void renderUi() {
		
		BetterUiGLTextureRenderer renderer = (BetterUiGLTextureRenderer)RendererManager.getInstance().getRenderer();
		
		for(DisplayUiElement duie : mDisplays) {
			renderer.drawUI(duie.mGeo);
		}
		
		if(mDrawInputAreas) {
			for(InputUiElement iuie : mInputs) {
				renderer.drawUI(iuie.mGeo);
			}
		}
		
	}
}
