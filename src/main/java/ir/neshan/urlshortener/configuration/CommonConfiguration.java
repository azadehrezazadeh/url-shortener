package ir.neshan.urlshortener.configuration;

import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.*;
import org.springframework.transaction.annotation.*;

@Configuration
@EnableJpaRepositories("ir.neshan.urlshortener.repository")
@EnableTransactionManagement
public class CommonConfiguration {
}
