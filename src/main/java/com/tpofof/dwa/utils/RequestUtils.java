package com.tpofof.dwa.utils;

import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class RequestUtils {

	public int limit(Optional<Integer> limit) {
		return getOptionalInt(limit, -1);
	}
	
	public int offset(Optional<Integer> offset) {
		return getOptionalInt(offset, 0);
	}
	
	public int getOptionalInt(Optional<Integer> val, int defaultVal) {
		return val != null && val.isPresent() && val.get() > 0 ? val.get() : defaultVal;
	}
}
