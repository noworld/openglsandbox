package com.solesurvivor.simplerender2_5.scene;

import com.solesurvivor.simplerender2_5.scene.animation.Armature;

public interface DrawableBones extends Drawable {

	public boolean getBones();
	public int getBoneCountSize();
	public int getBoneCountOffset();
	public int getBoneIndexSize();
	public int getBoneIndexOffset();
	public int getBoneWeightSize();
	public int getBoneWeightOffset();
	public Armature getArmature();
	
}
