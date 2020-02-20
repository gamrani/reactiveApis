package com.wiredbraincoffee.productapifunctional;

import com.wiredbraincoffee.productapifunctional.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Router {

    @Bean
    RouterFunction<ServerResponse> routes(ProductHandler handler) {
	/**return route(GET("/products").and(accept(APPLICATION_JSON)), handler::getAllProducts)
			.andRoute(POST("/products").and(contentType(APPLICATION_JSON)), handler::saveProduct)
			.andRoute(DELETE("/products").and(accept(APPLICATION_JSON)), handler::deleteAllProducts)
			.andRoute(GET("/products/events").and(accept(TEXT_EVENT_STREAM)), handler::getProductEvents)
			.andRoute(GET("/products/{id}").and(accept(APPLICATION_JSON)), handler::getProduct)
				.andRoute(PUT("/products/{id}").and(contentType(APPLICATION_JSON)), handler::updateProduct)
				.andRoute(DELETE("/products/{id}").and(accept(APPLICATION_JSON)), handler::deleteProduct);**/

       return nest(
                path("/products"),
                nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
                        route(GET("/"), handler::getAllProducts)
                                .andRoute(method(HttpMethod.POST), handler::saveProduct)
                                .andRoute(DELETE("/"), handler::deleteAllProducts)
                                .andRoute(GET("/events"), handler::getProductEvents)
                                .andNest(path("/{id}"),
                                        route(method(HttpMethod.GET), handler::getProduct)
                                                .andRoute(method(HttpMethod.PUT), handler::updateProduct)
                                                .andRoute(method(HttpMethod.DELETE), handler::deleteProduct)
                                )
                )
        );
    }
}
