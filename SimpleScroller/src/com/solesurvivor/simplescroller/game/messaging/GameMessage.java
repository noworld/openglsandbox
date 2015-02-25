package com.solesurvivor.simplescroller.game.messaging;

import com.solesurvivor.simplescroller.game.GameWorld;


public class GameMessage implements Comparable<GameMessage> {

	private int sender;
	private int receiver;
	long time;
	private GameMessageEnum message;
	
	private Object data;

	public GameMessage(int sender, int receiver, GameMessageEnum message, Object data) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.data = data;
		this.time = GameWorld.inst().getGameT();
	}
	

	public GameMessage(int sender, int receiver, long delay, GameMessageEnum message, Object data) {
		this(sender, receiver, message, data);
		this.time += delay;
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public GameMessageEnum getMessage() {
		return message;
	}

	public void setMessage(GameMessageEnum message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public int compareTo(GameMessage another) {
		return (int)(this.time - another.getTime());
	}
	
}
