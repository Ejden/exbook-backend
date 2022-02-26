package pl.exbook.exbook.security.adapter.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthEndpoint {

    @PreAuthorize("permitAll()")
    @PostMapping("login")
    fun signIn(@RequestBody loginCredentials: LoginCredentials) {}
}

data class LoginCredentials(val username: String, val password: String)

