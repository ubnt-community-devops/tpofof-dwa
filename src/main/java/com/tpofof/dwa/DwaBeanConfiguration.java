package com.tpofof.dwa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.tpofof.dwa.config.DwaConfiguration;
import com.tpofof.dwa.resources.PingResource;

@Configuration
public class DwaBeanConfiguration {

	@Bean(name="pingResource")
	@Scope("singleton")
	public PingResource pingResource() {
		System.out.println("pingResource");
		return new PingResource();
	}
	
	@Bean(name="dwaApp")
	@Scope("singleton")
	public DwaApp<DwaConfiguration> dwaApp() {
		System.out.println("dwaApp");
		return new DwaApp<DwaConfiguration>();
	}
}
