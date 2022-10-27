package com.netease.cloud.nsf.webflux.provider.web.exception;

public class StockException extends Exception {

	private static final long serialVersionUID = -3204299845896639994L;

	public StockException(String msg) {
		super(msg);
	}
	
	public StockException(String msg, Throwable t) {
		super(msg, t);
	}
}
