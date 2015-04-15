package com.tpofof.dwa.error;

public class HttpForbiddenException extends HttpCodeException {

	private static final long serialVersionUID = 1L;

	public HttpForbiddenException(String message) {
		super(403, message);
	}

}
