package com.tpofof.dwa.error;

public class HttpBadRequestException extends HttpCodeException {

	private static final long serialVersionUID = 1L;

	public HttpBadRequestException(String message) {
		super(400, message);
	}

}
