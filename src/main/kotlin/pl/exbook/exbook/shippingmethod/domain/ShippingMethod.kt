package pl.exbook.exbook.shippingmethod.domain

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.Money

class ShippingMethod(
    val id: ShippingMethodId,
    val methodName: String,
    val pickupPointMethod: Boolean,
    val defaultCost: Cost
)

data class Cost(
    val cost: Money,
    val canBeOverridden: Boolean = true
)

enum class Currency {
    PLN
}
