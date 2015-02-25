package com.solesurvivor.simplescroller.game.messaging;

import java.util.PriorityQueue;

import com.solesurvivor.simplescroller.game.GameWorld;

public class GameMessageBus {
	
	private static PriorityQueue<GameMessage> bus = new PriorityQueue<GameMessage>();
	
	public static GameMessage dispatch(int sender, int receiver, GameMessageEnum message, Object data) {
		GameMessage m = new GameMessage(sender, receiver, message, data);
		bus.add(m);
		return m;
	}
	
	public static GameMessage dispatch(int sender, int receiver, GameMessageEnum message) {
		return dispatch(sender, receiver, message, null);
	}
	
	public static GameMessage dispatch(int sender, int receiver, long delay, GameMessageEnum message, Object data) {
		GameMessage m = new GameMessage(sender, receiver, delay, message, data);
		bus.add(m);
		return m;
	}
	
	public static GameMessage dispatch(int sender, int receiver, long delay, GameMessageEnum message) {
		return dispatch(sender, receiver, delay, message, null);
	}
	
	public static boolean cancel(GameMessage message) {
		return bus.remove(message);
	}
	
	public static void update() {

		if(bus.size() < 1) {
			return;
		}
		
		long time = GameWorld.inst().getGameT();
		GameMessage m = bus.peek();
		while(m != null && m.getTime() <= time) {
			m = bus.poll();

			MessageReceiver mr = GameWorld.inst().getCurrentState().getDirectory().get(m.getReceiver());
			
			if(mr != null) {
				mr.receive(m);
			}
			
			m = bus.peek();
		}
	}


}
