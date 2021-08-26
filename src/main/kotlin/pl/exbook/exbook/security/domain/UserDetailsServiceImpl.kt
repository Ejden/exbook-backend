package pl.exbook.exbook.security.domain

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import pl.exbook.exbook.user.UserFacade

@Service
class UserDetailsServiceImpl(private val userFacade: UserFacade) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException(username)
        }

        return UserDetailsImpl(userFacade.getUserByUsername(username))
    }
}
