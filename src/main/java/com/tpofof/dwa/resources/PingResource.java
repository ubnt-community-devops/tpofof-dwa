package com.tpofof.dwa.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;

@Path("/ping")
@Produces(MediaType.TEXT_PLAIN)
@Component("pingResource")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class PingResource {

	@GET
	@Timed
	public String ping() {
		return "pong";
	}
}
