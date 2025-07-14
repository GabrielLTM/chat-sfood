package com.cadeia.ia.chatbotsfood.config;

import com.cadeia.ia.chatbotsfood.entity.Product;
import com.cadeia.ia.chatbotsfood.repository.ProductRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionToolConfiguration {

    private final ProductRepository productRepository;

    public FunctionToolConfiguration(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public record ProductRequest(String productName){}
    public record ProductResponse(String productName, String description, double price){}

    public static class MockProductService implements Function<ProductRequest, ProductResponse> {
        private final ProductRepository productRepository;

        public MockProductService(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @Override
        public ProductResponse apply(ProductRequest productRequest) {
            var product = productRepository.findByName(productRequest.productName());
            return new ProductResponse(productRequest.productName(), product.getDescription(), product.getPrice());
        }
    }

    @Description("Busca o produto pelo nome")
    public Function<ProductRequest, ProductResponse> productFunction() {
        return new MockProductService(productRepository);
    }
}
