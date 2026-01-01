package com.ecom.product_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.ProductStatus;

@Repository
public interface ProductStatusRepository extends JpaRepository<ProductStatus, Long> {

    @Query("SELECT ps FROM ProductStatus ps ORDER BY ps.displayOrder ASC")
    List<ProductStatus> findAllOrderByDisplayOrder();
}
