package joydeep.poc.cloudgateway.configurations;

import java.util.Date;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudGatewayConfigurations {

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	    return builder.routes()
	        .route(p -> p
	            .path("/joydeep/accounts-microservice/**")
	            .filters(f -> f.rewritePath("/joydeep/accounts-microservice/(?<segment>.*)","/${segment}")
	            				.addResponseHeader("X-Response-Time",new Date().toString()))
	            .uri("lb://ACCOUNTS-MICROSERVICE")).
	        route(p -> p
		            .path("/joydeep/loans-microservice/**")
		            .filters(f -> f.rewritePath("/joydeep/loans-microservice/(?<segment>.*)","/${segment}")
		            		.addResponseHeader("X-Response-Time",new Date().toString()))
		            .uri("lb://LOANS-MICROSERVICE")).
	        route(p -> p
		            .path("/joydeep/cards-microservice/**")
		            .filters(f -> f.rewritePath("/joydeep/cards-microservice/(?<segment>.*)","/${segment}")
		            		.addResponseHeader("X-Response-Time",new Date().toString()))
		            .uri("lb://CARDS-MICROSERVICE")).build();
	}
	
}
