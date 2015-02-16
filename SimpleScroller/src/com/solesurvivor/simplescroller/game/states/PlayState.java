package com.solesurvivor.simplescroller.game.states;

import android.graphics.Point;

import com.solesurvivor.simplescroller.commands.CommandEnum;
import com.solesurvivor.simplescroller.input.BackButtonInputHandler;
import com.solesurvivor.simplescroller.scene.water.Water;
import com.solesurvivor.util.math.Vec3;

public class PlayState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = PlayState.class.getSimpleName();
	
	protected Water water;
	
	public PlayState() {
		super();
		BackButtonInputHandler bbih = new BackButtonInputHandler();
		bbih.registerCommand(CommandEnum.REVERT_STATE.getCommand());
		inputHandlers.add(bbih);
		bgColor = new Vec3(0.059f, 0.056f, 0.655f);
	}

	@Override
	public void enter() {
		super.enter();
		if(water == null) {
			water = new Water(new Point(1,1));
			scene.addChild(water);
		}		
	}
	
	@Override
	public void execute() {
		super.execute();
	}
	
	@Override
	public void render() {		
		super.render();
	}
	
	@Override
	public void exit() {
		super.exit();
	}

}
