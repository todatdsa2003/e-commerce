package com.ecom.product_service.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.dto.CategoryRequestTest;

@RestController
@RequestMapping("/test/categories")
public class TestController {

    @PostMapping
    public String createCategory(@RequestBody CategoryRequestTest request) {
        return "Create Category: name=" + request.getName() + ", slug=" + request.getSlug();
    }

    @GetMapping
    public String getAllCategories() {
        return "Get All Categories: OK";
    }


    @GetMapping("/{id}")
    public String getCategoryById(@PathVariable String id) {
        return "Get Category with ID = " + id;
    }

    @PutMapping("/{id}")
    public String updateCategory(
            @PathVariable String id,
            @RequestBody CategoryRequestTest request) {
        return "Update Category ID=" + id +
                " -> new name=" + request.getName() +
                ", new slug=" + request.getSlug();
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable String id) {
        return "Delete Category with ID = " + id;
    }
}
