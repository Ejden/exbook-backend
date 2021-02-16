package pl.exbook.exbook.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth")
class AuthController {

    @PostMapping("signup")
    fun signUp() {

    }

    @PostMapping("signin")
    fun signIn() {

    }
}