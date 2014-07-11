package com.solesurvivor.simplerender.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Point;

import com.solesurvivor.simplerender.Geometry;
import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.RendererManager;

public class UiManager {
	
	private static final String INPUTAREA_SUFFIX = "_inputarea";
	
	private static UiManager mInstance = null;
	
	private Map<String,InputUiElement> mInputs = new HashMap<String,InputUiElement>();
	private List<DisplayUiElement> mDisplays = new ArrayList<DisplayUiElement>();
	
	public boolean mDrawInputAreas = true;
	
	private UiManager(List<Geometry> uis) {
		loadUi(uis);
	}
	
	public static void init(List<Geometry> uis) {
		if(mInstance == null) {
			mInstance = new UiManager(uis);
		}
	}
	
	public static UiManager getInstance() {
		return mInstance;
	}

	private void loadUi(List<Geometry> uis) {
		
		Command logger = new LoggingCommand();
		
		for(Geometry geo : uis) {
			
			if(geo.mName.contains(INPUTAREA_SUFFIX)) {
				//If the suffix is included
				//then we are dealing with an input area
				InputUiElement iuie = new InputUiElement(geo, geo.mInputArea);
				iuie.registerCommand("Logger", logger);
				mInputs.put(geo.mName, iuie);				
			} else {
				DisplayUiElement duie = new DisplayUiElement(geo);
				mDisplays.add(duie);
			}
		}
	}
	
	public void inputEvent(Point p) {
		for(InputUiElement iuie : mInputs.values()) {
			iuie.inputEvent(p);
		}
	}
	
	public void renderUi() {
		
		BetterUiGLTextureRenderer renderer = (BetterUiGLTextureRenderer)RendererManager.getInstance().getRenderer();
		
		for(DisplayUiElement duie : mDisplays) {
			renderer.drawUI(duie.getGeometry());
		}
		
		if(mDrawInputAreas) {
			for(InputUiElement iuie : mInputs.values()) {
				renderer.drawUI(iuie.getGeometry());
			}
		}
		
	}
}
