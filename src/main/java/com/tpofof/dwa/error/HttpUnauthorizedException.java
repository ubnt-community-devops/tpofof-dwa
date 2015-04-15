package com.tpofof.dwa.error;

public class HttpUnauthorizedException extends HttpCodeException {

	private static final long serialVersionUID = 1L;

	public HttpUnauthorizedException(String message) {
		super(401, message);
	}

}
