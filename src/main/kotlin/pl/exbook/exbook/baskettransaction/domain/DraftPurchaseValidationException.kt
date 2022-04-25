package pl.exbook.exbook.baskettransaction.domain

import pl.exbook.exbook.shared.ValidationException

class DraftPurchaseValidationException(msg: String? = null) : ValidationException(msg)
