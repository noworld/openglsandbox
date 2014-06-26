package com.solesurvivor.model.exceptions;

public class SizeLimitExceededException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SizeLimitExceededException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SizeLimitExceededException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public SizeLimitExceededException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public SizeLimitExceededException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public SizeLimitExceededException(String arg0, Throwable arg1, Object... args) {
		super(String.format(arg0, args), arg1);
	}
	
	public SizeLimitExceededException(String arg0, Object... args) {
		super(String.format(arg0, args));
	}

	
}
