package discord.api.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

// http://localhost:8080/swagger-ui/index.html
@OpenAPIDefinition(
        info = @Info(title = "Discord Server 명세서"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
}
