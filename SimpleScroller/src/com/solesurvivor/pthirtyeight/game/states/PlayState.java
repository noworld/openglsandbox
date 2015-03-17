package com.solesurvivor.pthirtyeight.game.states;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.solesurvivor.pthirtyeight.commands.CommandEnum;
import com.solesurvivor.pthirtyeight.fileio.GeometryIO;
import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessage;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageBus;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageEnum;
import com.solesurvivor.pthirtyeight.input.BackButtonInputHandler;
import com.solesurvivor.pthirtyeight.input.InputUiElement;
import com.solesurvivor.pthirtyeight.physics.CollisionPhysicsProcessor;
import com.solesurvivor.pthirtyeight.physics.PhysicsProcessor;
import com.solesurvivor.pthirtyeight.physics.PlayerPhysics;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.pthirtyeight.scene.Geometry;
import com.solesurvivor.pthirtyeight.scene.Node;
import com.solesurvivor.pthirtyeight.scene.gameobjects.GameObjectTypesEnum;
import com.solesurvivor.pthirtyeight.scene.gameobjects.MapManager;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.CountdownAnimation;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.PlayerFlying;
import com.solesurvivor.pthirtyeight.scene.gameobjects.levels.GameMap;
import com.solesurvivor.pthirtyeight.scene.gameobjects.levels.MapCell;
import com.solesurvivor.pthirtyeight.scene.gameobjects.spawn.EnemySpawner;
import com.solesurvivor.pthirtyeight.scene.water.Water;
import com.solesurvivor.simplescroller.R;
import com.solesurvivor.util.math.Vec3;

public class PlayState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = PlayState.class.getSimpleName();

	protected Water water;
	protected int playerIndex;
	protected List<SpriteNode> physicsObjects;
	protected PhysicsProcessor pp;
	protected EnemySpawner enemySpawner = null;
	protected GameMap map = null;
	
	public PlayState() {
		super();
		
		physicsObjects = new ArrayList<SpriteNode>();
		
		try {
			List<InputUiElement> uiElements = GeometryIO.loadInputUiElements(R.raw.gray_ui);
			
			Vec3 pushback = new Vec3(0.0f, 0.0f, -5.0f);
			for(InputUiElement iu : uiElements) {
				iu.translate(pushback.getX(), pushback.getY(), pushback.getZ());
			}
			inputHandlers.addAll(uiElements);
			
			BackButtonInputHandler bbih = new BackButtonInputHandler();
			bbih.registerCommand(CommandEnum.CREATE_ENEMY.getCommand());
			inputHandlers.add(bbih);
			
			List<Geometry> uiGeos = GeometryIO.loadUiElements(R.raw.gray_ui);
			for(Geometry g : uiGeos) {
				g.translate(pushback);
				gameObjects.append(g.getName().hashCode(), g);
			}
			ui.addAll(uiGeos);			
			
			water = new Water(new Point(1,1));
			scene.addChild(water);
			gameObjects.append("water".hashCode(), water);
			
			
			
			gameObjects.append(name.hashCode(), this);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pp = new CollisionPhysicsProcessor();		

		bgColor = new Vec3(0.059f, 0.056f, 0.655f);
	}

	private void spawnPlayer() {
		SpriteNode playerAc = new SpriteNode("player_ac", GameWorld.SCALE, GameObjectTypesEnum.PLAYER);
		playerIndex = playerAc.getName().hashCode();
		playerAc.setPhysics(new PlayerPhysics());
		scene.addChild(playerAc);
		gameObjects.append(playerAc.getName().hashCode(), playerAc);
		physicsObjects.add(playerAc);
		PlayerFlying pfs = new PlayerFlying(this);
		playerAc.changeState(pfs);
	}
	
	private void enableSpawner() {
		if(enemySpawner == null) {
			enemySpawner = new EnemySpawner((SpriteNode)gameObjects.get(playerIndex));
		} else {
			enemySpawner.setPlayer((SpriteNode)gameObjects.get(playerIndex));
		}
		enemySpawner.turnOn();
	}

	@Override
	public void enter() {
		spawnPlayer();
		enableSpawner();
//		map = MapManager.getGameMap("map_01");
		super.enter();
	}
	
	@Override
	public void execute() {
		enemySpawner.update();
		pp.processPhysics(physicsObjects);
		Point viewport = RendererManager.getViewport();
		viewport.x = viewport.x/2;
		viewport.y = viewport.y/2;
//		map.calcViewable(viewport);
		super.execute();		
	}
	
	@Override
	public void render() {
//		List<MapCell> mapCells = map.getViewable();
//		
//		for(MapCell mc : mapCells) {
//			RendererManager.getRenderer().drawSprite(mc);
//		}
		
		super.render();
	}
	
	@Override
	public void exit() {
		super.exit();
	}
	
	@Override
	public void cullObject(Node n) {
		if(n instanceof SpriteNode) {
			SpriteNode sn = (SpriteNode)n;
			if(sn.getType().equals(GameObjectTypesEnum.PLAYER)) {
				enemySpawner.turnOff();
				int selfId = this.getName().hashCode();
				
				SpriteNode countdown = new SpriteNode("empty", GameWorld.SCALE * 2.0f, GameObjectTypesEnum.MESSAGE);
				countdown.changeState(new CountdownAnimation());
				scene.addChild(countdown);
				gameObjects.append(countdown.getName().hashCode(), countdown);
				GameMessageBus.dispatch(selfId, selfId, 3500, GameMessageEnum.RESPAWN);
			}
		} 
	
		super.cullObject(n);
		physicsObjects.remove(n);
		
	}
	
	public void addPysicsObject(SpriteNode sn) {
		physicsObjects.add(sn);
	}
	
	@Override
	public void receive(GameMessage message) {
		if(message != null) {
			if(message.getMessage().equals(GameMessageEnum.FIRE)) {
				SpriteNode s = (SpriteNode)message.getData();
				scene.addChild(s);
				gameObjects.append(s.getName().hashCode(), s);
				physicsObjects.add(s);
			} else if(message.getMessage().equals(GameMessageEnum.RESPAWN)) {
				spawnPlayer();
				enableSpawner();
			}
		}
	}

}
