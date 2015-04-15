package com.tpofof.dwa.error;

public class HttpInternalServerErrorException extends HttpCodeException {
	
	private static final long serialVersionUID = 1L;

	public HttpInternalServerErrorException(String message) {
		super(500, message);
	}
}
