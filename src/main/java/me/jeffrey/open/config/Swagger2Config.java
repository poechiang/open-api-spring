package me.jeffrey.open.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class Swagger2Config {
    
    @Bean
    public Docket docket() {
        
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(getDocHeadInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("me.jeffrey.open.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    
    private ApiInfo getDocHeadInfo() {
        
        return new ApiInfoBuilder()
                .title("open.jeffery.me的文档")
                .description("More description about the API")
                .version("1.0")
                .build();
    }
}