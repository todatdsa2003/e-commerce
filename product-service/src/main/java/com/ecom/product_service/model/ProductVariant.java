package com.ecom.product_service.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_variant_product"))
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;
    @Column(name = "variant_name", length = 200)
    private String variantName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "compare_at_price", precision = 12, scale = 2)
    private BigDecimal compareAtPrice;

    @Builder.Default
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Builder.Default
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold = 5;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "option_values", nullable = false, columnDefinition = "jsonb")
    private String optionValuesJson;

    @Builder.Default
    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Transient
    public boolean isOutOfStock() {
        return stockQuantity != null && stockQuantity <= 0;
    }

    @Transient
    public boolean isLowStock() {
        return stockQuantity != null && lowStockThreshold != null 
                && stockQuantity <= lowStockThreshold;
    }

    @Transient
    public BigDecimal getDiscountPercent() {
        if (compareAtPrice == null || compareAtPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        if (price == null || price.compareTo(compareAtPrice) >= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = compareAtPrice.subtract(price);
        return discount.multiply(new BigDecimal("100"))
                .divide(compareAtPrice, 2, RoundingMode.HALF_UP);
    }

    @Transient
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }
}
