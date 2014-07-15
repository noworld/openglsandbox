package com.pimphand.simplerender2.fsm;

public interface State<T> {

	public void enter(T target);
	
	public void execute(T target);
	
	public void exit(T target);
	
}
