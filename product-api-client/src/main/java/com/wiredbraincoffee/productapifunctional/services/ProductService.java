package com.wiredbraincoffee.productapifunctional.services;

import com.wiredbraincoffee.productapifunctional.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private WebClient webClient;

    public ProductService() {
    }

    public Mono<Product> saveNewProduct(Product productMono){
        return webClient
                .post()
                .body(Mono.just(productMono), Product.class)
                .exchange()
                .flatMap(response -> response.bodyToMono(Product.class))
                .doOnSuccess(o -> System.out.println("**********POST " + o));
    }

    public Mono<Product> getProductById(String id){
        return  webClient
                .get()
                .uri("/{id}",id)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(o -> System.out.println("**********GET: " + o));
    }

    public Flux<Product> getAllProducts() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(o -> System.out.println("**********GET: " + o));
    }
}
