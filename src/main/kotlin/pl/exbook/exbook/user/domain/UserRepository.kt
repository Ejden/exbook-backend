package pl.exbook.exbook.user.domain

import pl.exbook.exbook.shared.UserId

interface UserRepository {

    fun findById(userId: UserId): User?

    fun findByLogin(login: String): User?

    fun findByLoginOrEmail(login: String, email: String): User?

    fun insert(user: User): User

    fun save(user: User): User
}
