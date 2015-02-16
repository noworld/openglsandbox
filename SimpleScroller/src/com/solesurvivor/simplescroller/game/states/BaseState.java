package com.solesurvivor.simplescroller.game.states;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.os.SystemClock;

import com.solesurvivor.simplescroller.input.InputEventBus;
import com.solesurvivor.simplescroller.input.InputHandler;
import com.solesurvivor.simplescroller.input.InputUiElement;
import com.solesurvivor.simplescroller.rendering.RendererManager;
import com.solesurvivor.simplescroller.scene.Camera;
import com.solesurvivor.simplescroller.scene.Geometry;
import com.solesurvivor.simplescroller.scene.StatefulNodeImpl;
import com.solesurvivor.util.math.Vec3;


public class BaseState implements GameState {
	
	public static final boolean RENDER_INPUT_AREAS = false;

	@SuppressWarnings("unused")
	private static final String TAG = BaseState.class.getSimpleName();
	private static final Vec3 BG_COLOR = new Vec3(0.7f, 0.7f, 0.7f);
	
	protected Vec3 bgColor = BG_COLOR;
	protected Camera camera;
	protected float zoom;
	protected Long deltaT;
	protected Long lastT = SystemClock.uptimeMillis();
	protected StatefulNodeImpl scene = new StatefulNodeImpl();
	protected List<InputHandler> inputHandlers = new ArrayList<InputHandler>();
	protected List<Geometry> ui = new ArrayList<Geometry>();
	
	public BaseState() {
		camera = new Camera();
	}
	
	@Override
	public void enter() {
		RendererManager.getRenderer().setCurrentCamera(camera);
		RendererManager.getRenderer().setClearColor(bgColor);		
		RendererManager.getRenderer().initOpenGLDefault();
	}

	@Override
	public void execute() {
		long tempT = SystemClock.uptimeMillis();
		deltaT = tempT - lastT;
		lastT = tempT;
		InputEventBus.inst().executeCommands(inputHandlers);
		scene.update();
	}

	@Override
	public void render() {
		scene.render();
		for(Geometry g : ui) {
			RendererManager.getRenderer().drawUI(g);
		}
		
		if(RENDER_INPUT_AREAS) {
			for(InputHandler ih : inputHandlers) {
				if(ih instanceof InputUiElement) {
					RendererManager.getRenderer().drawUI(((InputUiElement)ih).getGeometry());
				}
			}
		}
	}

	@Override
	public void exit() {
		for(InputHandler ih : inputHandlers) {
			ih.quiet();
		}
		//Clear input events when leaving
		InputEventBus.inst().clear();
	}
	
	@Override
	public long getDeltaT() {
		return deltaT;
	}
	
	@Override
	public List<InputHandler> getInputs() {
		return inputHandlers;
	}
	
	@Override
	public List<Geometry> getUiElements() {
		return ui;
	}
	
	@Override
	public Point getViewport() {
		return camera.getViewport();
	}
	
	
	@Override
	public void resizeViewport(Point p) {
		camera.resizeViewport(p);
	}

	@Override
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	
	@Override
	public void rotateCurrentCamera(float angle, Vec3 rot) {
		this.camera.rotate(angle, rot);
	}

	@Override
	public void translateCurrentCamera(Vec3 trans) {
		this.camera.translate(trans);
	}

}
