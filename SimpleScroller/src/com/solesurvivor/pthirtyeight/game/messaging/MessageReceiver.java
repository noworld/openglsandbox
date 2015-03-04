package com.solesurvivor.pthirtyeight.game.messaging;

public interface MessageReceiver {

	public String getName();
	public void receive(GameMessage message);
	
}
