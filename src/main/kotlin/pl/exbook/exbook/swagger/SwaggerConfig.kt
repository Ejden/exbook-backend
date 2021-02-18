package pl.exbook.exbook.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.Collections.singletonList

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.OAS_30)
            .select()
//            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.regex("^(?!/(error).*$).*$"))
            .build()
            .securitySchemes(singletonList(createSchema()))
            .securityContexts(singletonList(createContext()))
    }

    private fun createContext(): SecurityContext? {
        return SecurityContext.builder()
            .securityReferences(createRef())
            .forPaths(PathSelectors.any())
            .build()
    }

    private fun createRef(): MutableList<SecurityReference>? {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes = arrayOf(authorizationScope)

        return singletonList(SecurityReference("Authorization", authorizationScopes))
    }

    private fun createSchema(): SecurityScheme {
        return ApiKey("Authorization", "Authorization", "header")
    }
}

