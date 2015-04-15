package com.tpofof.dwa.error;

public class HttpConflictException extends HttpCodeException {
	
	private static final long serialVersionUID = 1L;

	public HttpConflictException(String message) {
		super(409, message);
	}

}
