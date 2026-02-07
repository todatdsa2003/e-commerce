package com.ecom.product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// ============================================
// MICROSERVICES IMPORTS - COMMENTED FOR LOCAL TESTING
// Uncomment these when deploying in microservices environment
// ============================================
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;


// ============================================
// MICROSERVICES ANNOTATIONS - COMMENTED FOR LOCAL TESTING
// Uncomment these when deploying in microservices environment
// ============================================
//@EnableFeignClients
@SpringBootApplication
//@EnableDiscoveryClient
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
