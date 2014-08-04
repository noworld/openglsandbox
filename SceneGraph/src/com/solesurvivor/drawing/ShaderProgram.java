package com.solesurvivor.drawing;

import java.util.ArrayList;
import java.util.List;

public class ShaderProgram extends Shader {
	
	public List<Shader> mShaders = new ArrayList<Shader>();

	public int getInputIndex(String name) {
		int h = -1;
		
		if(mUnif.containsKey(name)) {
			h = mUnif.get(name).intValue();
		} else if(mAttr.containsKey(name)) {
			h = mAttr.get(name).intValue();
		}
		
		return h;
	}
}
