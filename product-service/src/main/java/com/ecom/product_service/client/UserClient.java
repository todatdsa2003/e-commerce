// package com.ecom.product_service.client;

// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.GetMapping;

// import com.ecom.product_service.dto.UserDTO;

// @FeignClient(name = "user-service") 
// public interface UserClient {

//     @GetMapping("/api/v1/users/me")
//     UserDTO getCurrentUser();
// }