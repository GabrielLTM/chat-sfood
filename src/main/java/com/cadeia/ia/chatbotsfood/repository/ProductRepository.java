package com.cadeia.ia.chatbotsfood.repository;

import com.cadeia.ia.chatbotsfood.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    Product findByName(String name);
}
