package pl.exbook.exbook.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pl.exbook.exbook.security.domain.JwtAuthenticationFilter
import pl.exbook.exbook.security.domain.JwtAuthorizationFilter
import pl.exbook.exbook.security.domain.RestAuthenticationFailureHandler
import pl.exbook.exbook.security.domain.RestAuthenticationSuccessHandler

@Configuration
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
class SecurityConfig (
    private val userDetailsServiceImpl: UserDetailsService,
    private val objectMapper: ObjectMapper,
    private val successHandler: RestAuthenticationSuccessHandler,
    private val failureHandler: RestAuthenticationFailureHandler,
    @Value("\${jwt.secret}") private val secret: String): WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsServiceImpl)
    }

    override fun configure(http: HttpSecurity?) {
        http?.cors()?.and()?.csrf()?.disable()
        http?.authorizeRequests()
            ?.antMatchers("/swagger-ui.html")?.permitAll()
            ?.antMatchers("/v2/api-docs")?.permitAll()
            ?.antMatchers("/configuration/ui")?.permitAll()
            ?.antMatchers("/configuration/security")?.permitAll()
            ?.antMatchers("/swagger-resources/configuration/ui")?.permitAll()
            ?.antMatchers("/swagger-resources/configuration/security")?.permitAll()
            ?.antMatchers("/v3/api-docs")?.permitAll()
            ?.antMatchers("/webjars/**")?.permitAll()
            ?.antMatchers("/swagger-ui/**")?.permitAll()
            ?.antMatchers("/swagger-resources")?.permitAll()
            ?.antMatchers("/swagger-ui/")?.permitAll()
            ?.anyRequest()?.permitAll()
            ?.and()
            ?.addFilter(authenticationFilter())
            ?.addFilter(JwtAuthorizationFilter(authenticationManager(), userDetailsService(), secret))
            ?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ?.and()
            ?.exceptionHandling()
            ?.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))

    }

    fun authenticationFilter() : JwtAuthenticationFilter {
        val authenticationFilter = JwtAuthenticationFilter(objectMapper)
        authenticationFilter.setAuthenticationSuccessHandler(successHandler)
        authenticationFilter.setAuthenticationFailureHandler(failureHandler)
        authenticationFilter.setAuthenticationManager(super.authenticationManager())
        authenticationFilter.setFilterProcessesUrl("/api/auth/login")
        return authenticationFilter
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object:WebMvcConfigurer{
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedMethods(HttpMethod.POST.name, HttpMethod.GET.name, HttpMethod.PUT.name, HttpMethod.DELETE.name, HttpMethod.OPTIONS.name)
                    .allowedHeaders("*", "Content-Type")
                    .allowedOrigins("http://localhost:8080")
                    .allowCredentials(true)
            }
        }
    }

    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
