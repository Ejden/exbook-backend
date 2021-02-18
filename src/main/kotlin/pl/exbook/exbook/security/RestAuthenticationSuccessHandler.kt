package pl.exbook.exbook.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RestAuthenticationSuccessHandler(
    @Value("\${jwt.expirationTime}") private val expirationTime: Long,
    @Value("\${jwt.secret}") private val secret: String
) : SimpleUrlAuthenticationSuccessHandler() {


    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val principal = authentication?.principal as UserDetailsImpl
        val token = JWT.create()
            .withSubject(principal.username)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(Algorithm.HMAC256(secret))

        response?.addHeader("Authorization", "Bearer $token")
    }
}
