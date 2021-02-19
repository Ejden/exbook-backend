package pl.exbook.exbook.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pl.exbook.exbook.repositories.UserRepository
import pl.exbook.exbook.services.UserService

@Service
class UserDetailsServiceImpl(private val userService: UserService) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException(username)
        }

        val databaseUser = userService.findUserByUsername(username)

        if (databaseUser == null) {
            throw UsernameNotFoundException(username)
        } else {
            return UserDetailsImpl(databaseUser)
        }
    }
}