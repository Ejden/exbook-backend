package pl.exbook.exbook.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/users")
class UserController {

    @GetMapping
    fun getUserDetails() {

    }

    @PostMapping
    fun createUser() {

    }
}