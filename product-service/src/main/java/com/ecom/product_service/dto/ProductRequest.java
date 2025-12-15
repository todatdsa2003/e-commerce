package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Positive(message = "Giá sản phẩm phải lớn hơn 0")
    @Digits(integer = 10, fraction = 2, message = "Giá sản phẩm không được lớn quá 1 tỷ và có tối đa 2 chữ số thập phân")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer availability;

    @NotNull(message = "Trạng thái sản phẩm không được để trống")
    private Long statusId;

    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private Long categoryId;

    private Long brandId;

    @Valid
    private List<ProductAttributeRequest> attributes = new ArrayList<>();
}
