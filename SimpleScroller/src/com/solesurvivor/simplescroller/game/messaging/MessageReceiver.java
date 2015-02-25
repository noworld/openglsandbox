package com.solesurvivor.simplescroller.game.messaging;

public interface MessageReceiver {

	public String getName();
	public void receive(GameMessage message);
	
}
