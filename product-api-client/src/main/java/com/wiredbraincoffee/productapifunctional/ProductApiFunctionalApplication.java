package com.wiredbraincoffee.productapifunctional;

import com.wiredbraincoffee.productapifunctional.model.Product;
import com.wiredbraincoffee.productapifunctional.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import com.wiredbraincoffee.productapifunctional.handler.ProductHandler;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.http.HttpMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ProductApiFunctionalApplication {

	@Value("${remote.api.url}")
	private String remoteApiUrl;
	private WebClient webClient;

	public static void main(String[] args) {
		SpringApplication.run(ProductApiFunctionalApplication.class, args);
	}

	@Bean
	public WebClient webClientCreation(){
		return this.webClient = WebClient.builder()
				.baseUrl(remoteApiUrl)
				.build();
	}

	@Bean
	CommandLineRunner init(/*ReactiveMongoOperations operations, */ProductRepository repository) {
		return args -> {
			Flux<Product> productFlux = Flux.just(
					new Product(null, "Big Latte", 2.99),
					new Product(null, "Big Decaf", 2.49),
					new Product(null, "Green Tea", 1.99))
					.flatMap(repository::save);

			productFlux
					.thenMany(repository.findAll())
					.subscribe(System.out::println);

            /*operations.collectionExists(Product.class)
                    .flatMap(exists -> exists ? operations.dropCollection(Product.class) : Mono.just(exists))
					.thenMany(v -> operations.createCollection(Product.class))
					.thenMany(productFlux)
					.thenMany(repository.findAll())
					.subscribe(System.out::println);*/
		};
	}

}
