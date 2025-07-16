package com.cadeia.ia.chatbotsfood.tools;

import com.cadeia.ia.chatbotsfood.entity.Product;
import com.cadeia.ia.chatbotsfood.repository.ProductRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Description("Ferramentas para consultar o catálogo de produtos")
public class ProductTool {

    private final ProductRepository productRepository;
    public ProductTool(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public record ProductListResponse(List<Product> products) {}

    @Tool(name = "findProductByName", description = "Busca as informações do produto a partir do nome")
    public ProductListResponse findByName(@ToolParam(description = "O nome do produto") String name) {
        System.out.println("EXECUTANDO TOOL: findProductsByName com o parâmetro: " + name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return new ProductListResponse(products);
    }
    

    @Tool(name = "findAllProducts",description = "Busca as informações de todos os produtos sem o usuario mencionar categoria")
    public ProductListResponse findAll() {
        System.out.println("EXECUTANDO TOOL: findAllProducts");
        List<Product> products = productRepository.findAll();
        return new ProductListResponse(products);
    }

    @Tool(name = "findAllProductsByCategory", description = "Busca os produtos de uma determinada categoria")
    public ProductListResponse findByCategory(@ToolParam(description = "A categoria do produto") String category){
        System.out.println("EXECUTANDO TOOL: findAllProductsByCategory com o parâmetro" + category);
        List<Product> products = productRepository.findByCategoryContainingIgnoreCase(category);
        return new ProductListResponse(products);
    }
}
