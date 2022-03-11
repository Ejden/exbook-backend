package pl.exbook.exbook.user.domain

import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.UserId

class UserNotFoundException(username: String): NotFoundException("User $username not found") {
    constructor(userId: UserId) : this(userId.raw)
}
