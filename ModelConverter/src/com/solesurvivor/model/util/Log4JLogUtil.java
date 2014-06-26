package com.solesurvivor.model.util;

import org.apache.log4j.Logger;

public class Log4JLogUtil {

	private Logger logger;
	
	public Log4JLogUtil(Class<?> clazz) {
		logger = Logger.getLogger(clazz);
	}
	
	public void d(String message) {
		if(logger.isDebugEnabled()) logger.debug(message);
	}
	
	public void t(String message) {
		if(logger.isTraceEnabled()) logger.trace(message);
	}
	
	public void i(String message) {
		if(logger.isInfoEnabled()) logger.info(message);
	}
	
	public void w(String message) {
		if(logger.isInfoEnabled()) logger.warn(message);
	}
	
	public void e(String message) {
		if(logger.isInfoEnabled()) logger.error(message);
	}
	
	public void f(String message) {
		if(logger.isInfoEnabled()) logger.fatal(message);
	}
	
	public void w(String message, Throwable t) {
		if(logger.isInfoEnabled()) logger.warn(message, t);
	}
	
	public void e(String message, Throwable t) {
		if(logger.isInfoEnabled()) logger.error(message, t);
	}
	
	public void f(String message, Throwable t) {
		if(logger.isInfoEnabled()) logger.fatal(message, t);
	}
	
	public void d(String message, Object... args) {
		if(logger.isDebugEnabled()) logger.debug(String.format(message, args));
	}
	
	public void t(String message, Object... args) {
		if(logger.isTraceEnabled()) logger.trace(String.format(message, args));
	}
	
	public void i(String message, Object... args) {
		if(logger.isInfoEnabled()) logger.info(String.format(message, args));
	}
	
	public void w(String message, Object... args) {
		if(logger.isInfoEnabled()) logger.warn(String.format(message, args));
	}
	
	public void e(String message, Object... args) {
		if(logger.isInfoEnabled()) logger.error(String.format(message, args));
	}
	
	public void f(String message, Object... args) {
		if(logger.isInfoEnabled()) logger.fatal(String.format(message, args));
	}
	
	public void w(String message, Throwable t, Object... args) {
		if(logger.isInfoEnabled()) logger.warn(String.format(message, args), t);
	}
	
	public void e(String message, Throwable t, Object... args) {
		if(logger.isInfoEnabled()) logger.error(String.format(message, args), t);
	}
	
	public void f(String message, Throwable t, Object... args) {
		if(logger.isInfoEnabled()) logger.fatal(String.format(message, args), t);
	}
	
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}
	
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
	
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
}
