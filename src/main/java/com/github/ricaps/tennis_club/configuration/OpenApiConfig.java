package com.github.ricaps.tennis_club.configuration;

import com.github.ricaps.tennis_club.api.shared.ErrorDto;
import com.github.ricaps.tennis_club.api.shared.FieldErrorDto;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
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
			ResolvedSchema fieldErrorSchema = ModelConverters.getInstance()
				.resolveAsResolvedSchema(new AnnotatedType(FieldErrorDto.class));

			ResolvedSchema errSchema = ModelConverters.getInstance()
				.resolveAsResolvedSchema(new AnnotatedType(ErrorDto.class));
			Content errorContent = new Content().addMediaType("application/json",
					new io.swagger.v3.oas.models.media.MediaType().schema(errSchema.schema));

			openApi.getComponents()
				.addSchemas("FieldErrorDto", fieldErrorSchema.schema)
				.addSecuritySchemes(SECURITY_SCHEME_BEARER,
						new SecurityScheme().type(SecurityScheme.Type.HTTP)
							.scheme(SECURITY_SCHEME_BEARER)
							.description("JWT Bearer security scheme available for all endpoints"));

			openApi.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_BEARER));

			addGlobalResponse(openApi, "401",
					new ApiResponse().description("You are not authorized to access this resource!")
						.content(errorContent));
			addGlobalResponse(openApi, "403",
					new ApiResponse().description("You don't have required permissions to access this resource!")
						.content(errorContent));

			addGlobalResponse(openApi, "400",
					new ApiResponse().description("Validation of request failed!").content(errorContent));

		};

	}

	private void addGlobalResponse(OpenAPI openApi, String operationName, ApiResponse apiResponse) {
		openApi.getPaths()
			.values()
			.forEach(path -> path.readOperations()
				.forEach(operation -> operation.getResponses().addApiResponse(operationName, apiResponse)));
	}

}
