package pl.exbook.exbook.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager?,
    private val userDetailsService: UserDetailsService,
    private val secret: String) : BasicAuthenticationFilter(authenticationManager) {

    private val TOKEN_HEADER = "Authorization"
    private val TOKEN_PREFIX = "Bearer "

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authentication = getAuthentication(request)

        if (authentication == null) {
            chain.doFilter(request, response)
            return
        }

        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest) : UsernamePasswordAuthenticationToken? {
        val token = request.cookies.firstOrNull { it.name == TOKEN_HEADER }?.value
        if (token != null) {
            val userName: String? = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .subject

            if (userName != null) {
                val userDetails = userDetailsService.loadUserByUsername(userName)
                return UsernamePasswordAuthenticationToken(userDetails.username, null, userDetails.authorities)
            }
        }

        return null
    }

}