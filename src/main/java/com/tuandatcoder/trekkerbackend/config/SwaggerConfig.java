package com.tuandatcoder.trekkerbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH = "BearerAuth";

    @Bean
    public OpenAPI trekkerOpenAPI() {
        return new OpenAPI()
                // ──────────────────────────────
                //      TREKKER API – GRAND TITLE
                // ──────────────────────────────
                .info(new Info()
                        .title("Trekker API")
                        .description("""
                                <h1> TREKKER – Conquer Every Trail </h1>
                                <p><strong>Backend REST API</strong> for the ultimate trekking & outdoor adventure platform.</p>
                                <p>Explore routes, connect with fellow trekkers, and conquer the mountains – all powered by this API.</p>
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Tuan Dat – Lead Developer")
                                .email("tuandatdq03@gmail.com")
                                .url("https://github.com/tuandatcoder")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )

                // Global security – adds lock icons + Authorize button
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))

                // JWT Bearer Authentication Scheme
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("""
                                        <strong>Enter your JWT token below</strong><br><br>
                                        1. Login via <code>/api/auth/login</code> or Google OAuth<br>
                                        2. Copy the <code>accessToken</code><br>
                                        3. Paste here (with or without <code>Bearer </code> prefix)<br><br>
                                        Example:<br>
                                        <code>Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIi...</code>
                                        """)
                        )
                );
    }
}