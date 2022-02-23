package pl.exbook.exbook.adapters

import pl.exbook.exbook.exceptions.UserAlreadyExistsException
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserRepository

class InMemoryUserRepository : UserRepository {
    private val memory = mutableMapOf<UserId, User>()

    override fun findById(userId: UserId): User? {
        return memory[userId]
    }

    override fun findByLogin(login: String): User? {
        return memory.values.firstOrNull{ it.username == login }
    }

    override fun findByLoginOrEmail(login: String, email: String): User? {
        return memory.values.firstOrNull { it.username == login || it.email == email }
    }

    override fun insert(user: User): User {
        if (memory[user.id] != null) {
            throw UserAlreadyExistsException()
        }

        memory[user.id] = user
        return user
    }
}
