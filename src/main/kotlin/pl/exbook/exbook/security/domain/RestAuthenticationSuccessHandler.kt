package pl.exbook.exbook.security.domain

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList

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
        val authList = ArrayList<String>()
        authList.addAll(
            principal.authorities.stream()
            .map { authority -> authority.authority }
            .collect(Collectors.toList())
        )

        val token = JWT.create()
            .withSubject(principal.username)
            .withClaim("authorities", authList.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(Algorithm.HMAC256(secret))

//        response?.addHeader("Set-Cookie", "Authorization=$token")
        val cookie = Cookie("Authorization", token)
        cookie.maxAge = expirationTime.toInt()
        cookie.secure = false
        cookie.isHttpOnly = false
        cookie.path = "/"

        response?.addCookie(cookie)
    }
}
