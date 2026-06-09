package com.tommy.identity.infrastructure.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // General configuaration
                .info(new Info()
                        .title("Identity & Profile Service API")
                        .description("Tài liệu API cho phân hệ Quản lý tài khoản, Phân quyền và Bảo mật MFA.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tommy")
                                .email("tommy.dev@gmail.com")))
                // Config JWT security
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Nhập Access Token của bạn vào đây để test các API được bảo vệ.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}