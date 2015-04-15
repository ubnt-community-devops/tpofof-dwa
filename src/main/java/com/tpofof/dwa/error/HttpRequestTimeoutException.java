package com.tpofof.dwa.error;

public class HttpRequestTimeoutException extends HttpCodeException {

	private static final long serialVersionUID = 1L;

	public HttpRequestTimeoutException(String message) {
		super(408, message);
	}

}
