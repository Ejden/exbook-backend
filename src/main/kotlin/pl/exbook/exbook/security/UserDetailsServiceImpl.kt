package pl.exbook.exbook.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pl.exbook.exbook.repositories.UserRepository

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException(username)
        }

        val user = userRepository.findByLogin(username)

        if (user == null) {
            throw UsernameNotFoundException(username)
        } else {
            return UserDetailsImpl(user)
        }
    }
}