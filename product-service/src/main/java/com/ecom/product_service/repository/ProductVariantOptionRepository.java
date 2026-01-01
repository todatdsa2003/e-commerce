package com.ecom.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.ProductVariantOption;

@Repository
public interface ProductVariantOptionRepository extends JpaRepository<ProductVariantOption, Long> {

    List<ProductVariantOption> findByProductIdOrderByDisplayOrder(Long productId);

    Optional<ProductVariantOption> findByProductIdAndOptionName(Long productId, String optionName);

    boolean existsByProductIdAndOptionName(Long productId, String optionName);

    void deleteByProductId(Long productId);

    long countByProductId(Long productId);
}
