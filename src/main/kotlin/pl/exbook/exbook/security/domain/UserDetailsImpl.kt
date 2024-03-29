package pl.exbook.exbook.security.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import pl.exbook.exbook.user.domain.User

class UserDetailsImpl(private var user : User) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.authorities
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun getUsername(): String? {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return user.active
    }

    override fun isAccountNonLocked(): Boolean {
        return !user.locked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return !user.credentialExpired
    }

    override fun isEnabled(): Boolean {
        return user.enabled
    }
}