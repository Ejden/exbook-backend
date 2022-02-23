package pl.exbook.exbook.security.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import pl.exbook.exbook.security.adapter.rest.LoginCredentials
import java.io.BufferedReader
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(private val objectMapper: ObjectMapper) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val reader: BufferedReader? = request?.reader
        val sb: StringBuilder = StringBuilder()
        var line: String? = ""

        while (true) {
            line = reader?.readLine()

            if (line != null)
                sb.append(line)
            else
                break
        }

        val cred = objectMapper.readValue(sb.toString(), LoginCredentials::class.java)
        val token = UsernamePasswordAuthenticationToken(cred.username, cred.password)
        setDetails(request, token)

        return this.authenticationManager.authenticate(token)
    }


}
