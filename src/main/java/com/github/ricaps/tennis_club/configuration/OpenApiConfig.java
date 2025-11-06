package com.github.ricaps.tennis_club.configuration;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

	public static final String SECURITY_SCHEME_BEARER = "Bearer";

	@Bean
	public OpenApiCustomizer openApi() {
		return openApi -> {
			openApi.getComponents()
				.addSecuritySchemes(SECURITY_SCHEME_BEARER,
						new SecurityScheme().type(SecurityScheme.Type.HTTP)
							.scheme(SECURITY_SCHEME_BEARER)
							.description("JWT Bearer security scheme available for all endpoints"));
			openApi.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_BEARER));
		};

	}

}
