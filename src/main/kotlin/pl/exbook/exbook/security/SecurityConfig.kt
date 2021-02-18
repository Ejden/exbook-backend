package pl.exbook.exbook.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
class SecurityConfig (
    private val userDetailsServiceImpl: UserDetailsService,
    private val objectMapper: ObjectMapper,
    private val successHandler: RestAuthenticationSuccessHandler,
    private val failureHandler: RestAuthenticationFailureHandler,
    @Value("\${jwt.secret}") private val secret: String): WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsServiceImpl)?.passwordEncoder(NoOpPasswordEncoder.getInstance())
    }

    override fun configure(http: HttpSecurity?) {
        http?.csrf()?.disable()
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
            ?.anyRequest()?.authenticated()
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
        authenticationFilter.setFilterProcessesUrl("/api/v1/auth/login")
        return authenticationFilter
    }
}
