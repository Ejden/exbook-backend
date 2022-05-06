package pl.exbook.exbook.order.domain

import pl.exbook.exbook.shared.ValidationException

class IllegalStatusChangeException(message: String? = null) : ValidationException(message)
