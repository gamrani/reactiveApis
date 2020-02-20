package com.wiredbraincoffee.productapifunctional.handler;

import com.wiredbraincoffee.productapifunctional.model.Product;
import com.wiredbraincoffee.productapifunctional.model.ProductEvent;
import com.wiredbraincoffee.productapifunctional.repository.ProductRepository;
import com.wiredbraincoffee.productapifunctional.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductHandler {

    private ProductRepository repository;
    private WebClient webClient;
    @Autowired
    private ProductService productService;
    @Value("${remote.api.url}")
    private String remoteApiUrl;

    public ProductHandler(ProductRepository repository) {

        this.webClient = WebClient.builder()
                .baseUrl(remoteApiUrl)
                .build();
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        Flux<Product> products = productService.getAllProducts();

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(products, Product.class);
    }

    public Mono<ServerResponse> getProduct(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Product> productMono = productService.getProductById(id);

        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono
                .flatMap(product ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(fromObject(product)))
                .doOnError((e -> System.out.println("######:"+e.getMessage())))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Product> existingProductMono = productService.getProductById(id);
        Mono<Product> productMono = request.bodyToMono(Product.class);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono.zipWith(existingProductMono, (product, existingProduct) ->
                                                              new Product(existingProduct.getId(),
                                                                      product.getName(), product.getPrice()))
                .flatMap(product ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(productService.saveNewProduct(product), Product.class)
                ).switchIfEmpty(notFound);
    }

















    public Mono<ServerResponse> saveProduct(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.flatMap(product ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(APPLICATION_JSON)
                        .body(repository.save(product), Product.class));
    }


    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Product> productMono = this.repository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono
                .flatMap(existingProduct ->
                        ServerResponse.ok()
                                .build(repository.delete(existingProduct))
                )
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteAllProducts(ServerRequest request) {
        return ServerResponse.ok()
                .build(repository.deleteAll());
    }

    public Mono<ServerResponse> getProductEvents(ServerRequest request) {
        Flux<ProductEvent> eventsFlux =
                Flux.interval(Duration.ofSeconds(1))
                .map(val -> new ProductEvent(val, "Product Event")
        );

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventsFlux, ProductEvent.class);
    }

}
