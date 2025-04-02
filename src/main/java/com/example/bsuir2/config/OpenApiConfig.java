package com.example.bsuir2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.parameters.Parameter;

import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://api.prod-SvyatSam.example.com").description("Production Server"),
                        new Server().url("https://api.dev-SvyatSam.example.com").description("Development Server")
                ))
                .info(new Info()
                        .title("BSUIR API — корпоративная интеграция «SvyatSam»")
                        .description("**Расширенная документация** для BSUIR API, применяемая в экосистеме крупного корпоративного решения. \n\n"
                                + "**Основные разделы и возможности:**\n"
                                + "1. **CRUD**-операции для студентов и групп.\n"
                                + "2. Интеграция с внешним API БГУИР для получения свежих данных.\n"
                                + "3. Гибкая фильтрация и поиск.\n"
                                + "4. Расширенная безопасность и аудит запросов.\n\n"
                                + "**Важно:**\n"
                                + "- Все запросы должны содержать корректные заголовки безопасности.\n"
                                + "- При превышении квоты запросов применяется механизм rate-limiting.\n"
                                + "- Для полного списка статусов ошибок см. внешний раздел документации.\n\n"
                                + "При возникновении вопросов обратитесь к контактному лицу ниже.")
                        .version("2.1.0-ALPHA")
                        .termsOfService("https://SvyatSam.example.com/terms-of-service")
                        .contact(new Contact()
                                .name("Святослав (ООО «SvyatSam»)")
                                .url("https://SvyatSam.example.com/developers")
                                .email("support@SvyatSam.example.com"))
                        .license(new License()
                                .name("Corporate License 2025")
                                .url("https://SvyatSam.example.com/license")))
                .externalDocs(new ExternalDocumentation()
                        .description("Полная техническая документация, FAQ, примеры кода")
                        .url("https://SvyatSam.example.com/docs"));
    }


    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (operation, handlerMethod) -> {
            final Parameter globalHeader = new Parameter()
                    .in("header")
                    .name("X-Global-Header")
                    .description("Глобальный заголовок, необходимый для внутренних политик безопасности. "
                            + "Обязательно указывать валидный токен или ключ.")
                    .required(true)
                    .schema(new StringSchema());


            operation.addParametersItem(globalHeader);

            return operation;
        };
    }
}

//http://localhost:8080/swagger-ui/index.html#/