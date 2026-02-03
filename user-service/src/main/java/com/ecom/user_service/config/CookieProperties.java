package com.ecom.user_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {

    private boolean httpOnly;
    private boolean secure;
    private String sameSite;
    private String path;
    private long accessTokenMaxAge;
    private long refreshTokenMaxAge;
}
