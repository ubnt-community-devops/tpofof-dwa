package com.tpofof.dwa.error;

public class HttpNotImplementedException extends HttpCodeException {

	private static final long serialVersionUID = 1L;

	public HttpNotImplementedException(String message) {
		super(501, message);
	}

}
