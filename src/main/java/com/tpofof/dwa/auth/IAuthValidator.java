package com.tpofof.dwa.auth;

import com.tpofof.dwa.error.HttpUnauthorizedException;

public interface IAuthValidator<AuthModelT, PrimaryKeyT, AuthRequestPermissionT> {
	
	public void validate(AuthModelT authModel, PrimaryKeyT assetKey, AuthRequestPermissionT permType) throws HttpUnauthorizedException;
}
