package ir.neshan.urlshortener.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Url Shortener API")
                        .description("Url Shortener System API")
                        .summary("")
                        .version("v0.1.0")
                        .contact(new Contact()
                                .email("azadeh.rezazadeh@gmail.com")
                                .name("azadeh rezazadeh")))
               .addServersItem(new Server()
                       .url("http://localhost:8082")
                       .description("Url Shortener API"));
    }
}
