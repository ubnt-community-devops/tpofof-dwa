package com.tpofof.dwa.auth;

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.DefaultUnauthorizedHandler;
import io.dropwizard.auth.UnauthorizedHandler;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class CookieAuthFactory<PrincipalT> extends AuthFactory<Map<String, String>, PrincipalT> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CookieAuthFactory.class);

	private final boolean required;
	private final Class<PrincipalT> generatedClass;
    private final String realm;
    private UnauthorizedHandler unauthorizedHandler = new DefaultUnauthorizedHandler();
    private String prefix;
    private HttpServletRequest request;
	
	public CookieAuthFactory(boolean required, Authenticator<Map<String, String>, PrincipalT> authenticator,
			String realm, String prefix, Class<PrincipalT> generatedClass) {
		super(authenticator);
		this.required = required;
		this.generatedClass = generatedClass;
		this.realm = realm;
		this.prefix = prefix;
	}

	public PrincipalT provide() {
		Optional<PrincipalT> oUser = Optional.absent();
		if (request != null) {
			Cookie[] cookies = request.getCookies();
			Map<String, String> cookiesMap = Maps.newHashMap();
			for (Cookie c : cookies) {
				cookiesMap.put(c.getName(), c.getValue());
			}
			try {
				oUser = authenticator().authenticate(cookiesMap);
			} catch (AuthenticationException e) {
				LOGGER.warn("Error authenticating request. " + cookiesMap, e);
				e.printStackTrace();
			}
		} else {
			LOGGER.warn("Request null while trying to authenticate.");
		}
		if (!oUser.isPresent() && required) {
			throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
		}
		return oUser.isPresent() ? oUser.get() : null;
	}

	public CookieAuthFactory<PrincipalT> prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
 
    public CookieAuthFactory<PrincipalT> responseBuilder(UnauthorizedHandler unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
        return this;
    }
 
    @Override
    public AuthFactory<Map<String, String>, PrincipalT> clone(boolean required) {
        boolean newRequired = this.required && required;
		return new CookieAuthFactory<PrincipalT>(newRequired, authenticator(), realm, prefix, generatedClass).responseBuilder(unauthorizedHandler);
    }
 
    @Override
    public void setRequest(HttpServletRequest request) {
    	this.request = request;
    }
    
    @Override
    public Class<PrincipalT> getGeneratedClass() {
        return generatedClass;
    }
}
