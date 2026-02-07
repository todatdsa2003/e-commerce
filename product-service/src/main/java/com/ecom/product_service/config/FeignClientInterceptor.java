// package com.ecom.product_service.config;

// import org.springframework.stereotype.Component;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;

// // import feign.RequestInterceptor;
// // import feign.RequestTemplate;
// import jakarta.servlet.http.HttpServletRequest;

// @Component
// public class FeignClientInterceptor implements RequestInterceptor {
//     @Override
//     public void apply(RequestTemplate template) {
//         // Lấy Token từ request hiện tại (do người dùng gửi lên)
//         ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//         if (attributes != null) {
//             HttpServletRequest request = attributes.getRequest();
//             String token = request.getHeader("Authorization");
            
//             // Tự động đẩy Token đó vào header của Feign Client
//             if (token != null) {
//                 template.header("Authorization", token);
//             }
//         }
//     }
// }