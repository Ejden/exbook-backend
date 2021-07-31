package pl.exbook.exbook.security.domain

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import pl.exbook.exbook.user.UserService

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