package com.solesurvivor.model.parsing;

import com.solesurvivor.scene.BufferedGeometry;

public class GeometryWithReadPostition extends BufferedGeometry {
	
	public int mPositionsReadCount = 0;
	public int mNormalsReadCount = 0;
	public int mTexcoordsReadCount = 0;
	public int mColorsReadCount = 0;
	
	public GeometryWithReadPostition() {
		super();
	}
	
}
