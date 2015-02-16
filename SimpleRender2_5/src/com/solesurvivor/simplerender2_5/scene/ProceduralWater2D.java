package com.solesurvivor.simplerender2_5.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.util.math.Vec3;

public class ProceduralWater2D extends ProceduralTexture2D {
	
	protected List<Wave> waves;
	protected List<Light> lights;

	public ProceduralWater2D(String shaderName, String textureName, Point dim, CoordinateSystemEnum texSystem) throws IOException {
		super(shaderName, textureName, dim, texSystem);
		
		lights = new ArrayList<Light>(1);
		Light l = new Light();
		l.mPosition = new float[]{1.0f, 5.0f, -5.0f, 1.0f};
		lights.add(l);
		
		 
		waves = new ArrayList<Wave>(2);
		float tsc = 0.5f;
		Wave wave = new Wave(0.05f, new Vec3(0.0f, 0.0f, 1.0f), 5.0f);		 
		Wave wave2 = new Wave(0.02f, new Vec3(0.25f, 0.0f, 0.70f), 10.6186f);
		wave.setTimeScale(tsc);
		wave2.setTimeScale(tsc);
		waves.add(wave);
		waves.add(wave2);
		
		
	}
	
	public List<Wave> getWaves() {
		return waves;
	}
	
	@Override
	public void render() {
		RendererManager.getRenderer().renderWaterTexture(this);
		for(Node n : mChildren) {
			n.render();
		}
	}
	
	@Override
	public List<Light> getLights() {
		return lights;
	}

}
