package com.tpofof.dwa;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.tpofof.core.App;
import com.tpofof.core.utils.json.ObjectMapperDecorator;
import com.tpofof.dwa.config.DwaConfiguration;
import com.tpofof.dwa.resources.PingResource;

@Component("dwaApp")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DwaApp<ConfigType extends DwaConfiguration> extends Application<ConfigType> {

	@Autowired private PingResource pingResource;
	@Autowired private ObjectMapperDecorator objectMapperDecorator;
	
	public static void main(String[] args) throws Exception {
		App.getContext().getBean(DwaApp.class).run(args);
	}
	
	@Override
	public String getName() {
		return "dwa-app";
	}
	
	@Override
	public void initialize(Bootstrap<ConfigType> bootstrap) {
		objectMapperDecorator.decorate(bootstrap.getObjectMapper());
	}
	
	@Override
	public void run(ConfigType config, Environment env) throws Exception {
		env.jersey().register(pingResource);
	}
	
	protected void addCorsSupport(Environment env) {
		/* CORS */
		final FilterRegistration.Dynamic cors = env.servlets().addFilter("CORS", CrossOriginFilter.class);

	    // Configure CORS parameters
	    cors.setInitParameter("allowedOrigins", "*");
	    cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
	    cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

	    // Add URL mapping
	    cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
	}
}
