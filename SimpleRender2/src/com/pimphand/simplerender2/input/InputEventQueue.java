package com.pimphand.simplerender2.input;

import java.util.PriorityQueue;
import java.util.Queue;


public class InputEventQueue {

	private static InputEventQueue sInstance = new InputEventQueue();
	
	private Queue<InputEvent> mQueue = new PriorityQueue<InputEvent>();
	
	private InputEventQueue() {
		
	}
	
	public static InputEventQueue inst() {
		return sInstance;
	}
	
	public InputEvent peek() {
		return mQueue.peek();
	}
	
	public InputEvent poll() {
		return mQueue.poll();
	}
	
	public boolean offer(InputEvent event) {
		return mQueue.offer(event);
	}
	
	public void clear() {
		mQueue.clear();
	}
}
