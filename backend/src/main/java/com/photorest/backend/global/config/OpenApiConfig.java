package com.photorest.backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI photorestOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Photorest API")
                        .description("Photorest 사진 보관 서비스 API 문서")
                        .version("v1"));
    }
}
