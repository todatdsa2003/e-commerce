package com.ecom.user_service.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Create custom ObjectMapper with string trimming
        ObjectMapper objectMapper = new ObjectMapper();
        
        SimpleModule stringTrimModule = new SimpleModule();
        stringTrimModule.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                return value != null ? value.trim() : null;
            }
        });
        
        objectMapper.registerModule(stringTrimModule);
        
        // Create and add custom JSON converter
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);
        converters.add(0, jsonConverter);
        
        log.info("=== WebConfig: Configured custom Jackson converter with String trimming ===");
    }
}
