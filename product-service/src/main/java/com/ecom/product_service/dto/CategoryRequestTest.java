package com.ecom.product_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CategoryRequestTest {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "parent_id")
    @JsonProperty("parent_id")
    private Long parentId;
}
