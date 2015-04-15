package com.tpofof.dwa.error;

public class HttpNotFoundException extends HttpCodeException {

	private static final long serialVersionUID = 1L;

	public HttpNotFoundException(String message) {
		super(404, message);
	}
}
