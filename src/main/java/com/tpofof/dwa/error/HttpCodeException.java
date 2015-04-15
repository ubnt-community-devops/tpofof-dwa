package com.tpofof.dwa.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class HttpCodeException extends Throwable {

	private static final long serialVersionUID = -6084409902040121326L;

	private final int httpResponseCode;
	private final String message;
}
