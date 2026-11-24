package tienda.neonfoxgaming.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    OpenAPI neonFoxOpenApi() {
        return new OpenAPI().components(new Components().addSecuritySchemes("basicAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info()
                .title("Neon Fox Gaming API")
                .version("1.0")
                .description("API de catálogo, usuarios y pedidos de Neon Fox Gaming."));
    }
}
