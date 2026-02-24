package com.ecom.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.product_service.model.ProductVariantOption;

@Repository
public interface ProductVariantOptionRepository extends JpaRepository<ProductVariantOption, Long> {

    List<ProductVariantOption> findByProductIdOrderByDisplayOrder(Long productId);

    Optional<ProductVariantOption> findByProductIdAndOptionName(Long productId, String optionName);

    boolean existsByProductIdAndOptionName(Long productId, String optionName);

    void deleteByProductId(Long productId);

    long countByProductId(Long productId);

    /**
     * Appends a single value to the option_values TEXT[] array using PostgreSQL native array_append().
     * Bypasses Hibernate entity tracking to avoid INSERT/UPDATE issues with TEXT[] columns.
     * clearAutomatically = true evicts the stale entity from the first-level cache after update.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE product_variant_options SET option_values = array_append(option_values, :value) WHERE id = :id",
           nativeQuery = true)
    void appendOptionValue(@Param("id") Long id, @Param("value") String value);

    /**
     * Removes a single value from the option_values TEXT[] array using PostgreSQL native array_remove().
     * Bypasses Hibernate entity tracking to avoid INSERT/UPDATE issues with TEXT[] columns.
     * clearAutomatically = true evicts the stale entity from the first-level cache after update.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE product_variant_options SET option_values = array_remove(option_values, :value) WHERE id = :id",
           nativeQuery = true)
    void removeOptionValue(@Param("id") Long id, @Param("value") String value);

    /**
     * Renames an option by updating the option_name column directly.
     * Bypasses Hibernate entity tracking for the same reason as above.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE product_variant_options SET option_name = :newName WHERE id = :id",
           nativeQuery = true)
    void updateOptionName(@Param("id") Long id, @Param("newName") String newName);
}
