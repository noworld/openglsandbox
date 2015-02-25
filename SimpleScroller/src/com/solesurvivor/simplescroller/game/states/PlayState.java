package com.solesurvivor.simplescroller.game.states;

import java.io.IOException;
import java.util.List;

import android.graphics.Point;

import com.solesurvivor.simplescroller.R;
import com.solesurvivor.simplescroller.fileio.GeometryIO;
import com.solesurvivor.simplescroller.game.messaging.GameMessage;
import com.solesurvivor.simplescroller.game.messaging.GameMessageEnum;
import com.solesurvivor.simplescroller.input.InputUiElement;
import com.solesurvivor.simplescroller.scene.Geometry;
import com.solesurvivor.simplescroller.scene.gameobjects.SpriteNode;
import com.solesurvivor.simplescroller.scene.gameobjects.behavior.PlayerFlyingState;
import com.solesurvivor.simplescroller.scene.water.Water;
import com.solesurvivor.util.math.Vec3;

public class PlayState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = PlayState.class.getSimpleName();

	protected Water water;
	protected int playerIndex;
	
	public PlayState() {
		super();
		
		try {
			List<InputUiElement> uiElements = GeometryIO.loadInputUiElements(R.raw.gray_ui);
			
			Vec3 pushback = new Vec3(0.0f, 0.0f, -5.0f);
			for(InputUiElement iu : uiElements) {
				iu.translate(pushback.getX(), pushback.getY(), pushback.getZ());
			}
			inputHandlers.addAll(uiElements);
			
			List<Geometry> uiGeos = GeometryIO.loadUiElements(R.raw.gray_ui);
			for(Geometry g : uiGeos) {
				g.translate(pushback);
				gameObjects.append(g.getName().hashCode(), g);
			}
			ui.addAll(uiGeos);			
			
			water = new Water(new Point(1,1));
			scene.addChild(water);
			gameObjects.append("water".hashCode(), water);
			
			SpriteNode enemy = new SpriteNode("enemy_1", 3.85f);
			scene.addChild(enemy);
			enemy.rotate(180.0f, new Vec3(0,0,1.0f));
			enemy.translate(new Vec3(100.0f, 100.0f, 0));
			
			SpriteNode playerAc = new SpriteNode("player_ac", 3.85f);
			playerIndex = playerAc.getName().hashCode();
			scene.addChild(playerAc);
			gameObjects.append(playerAc.getName().hashCode(), playerAc);
			
			gameObjects.append(name.hashCode(), this);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bgColor = new Vec3(0.059f, 0.056f, 0.655f);
	}

	@Override
	public void enter() {
		PlayerFlyingState pfs = new PlayerFlyingState(this);
		SpriteNode pa = (SpriteNode) gameObjects.get(playerIndex);
		pa.changeState(pfs);
		super.enter();
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
	
	@Override
	public void receive(GameMessage message) {
		if(message != null && message.getMessage().equals(GameMessageEnum.FIRE)) {
			SpriteNode s = (SpriteNode)message.getData();
			scene.addChild(s);
			gameObjects.append(s.getName().hashCode(), s);
		}
	}

}
