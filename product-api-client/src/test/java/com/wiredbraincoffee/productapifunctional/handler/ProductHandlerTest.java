package com.wiredbraincoffee.productapifunctional.handler;

import com.wiredbraincoffee.productapifunctional.model.Product;
import com.wiredbraincoffee.productapifunctional.repository.ProductRepository;
import com.wiredbraincoffee.productapifunctional.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductHandlerTest {

    private WebTestClient client;

    private List<Product> expectedList;

    @Autowired
    private ProductRepository repository;

    @LocalServerPort
    private int port;
    @Autowired
    private ProductService productService;

    @BeforeEach
    public void beforeEach() {
        this.client = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port + "/products")
                .build();

        this.expectedList = productService.getAllProducts()
                .toStream()
                .collect(Collectors.toList());
    }

    @Test
    public void testGetAllProducts() {
        client
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedList);
    }

}