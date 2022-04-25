package pl.exbook.exbook.shippingmethod.domain

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.Money

class ShippingMethod(
    val id: ShippingMethodId,
    val methodName: String,
    val type: ShippingMethodType,
    val defaultCost: Cost
)

enum class ShippingMethodType {
    PICKUP_DELIVERY,
    ADDRESS_DELIVERY,
    PERSONAL_DELIVERY
}

data class Cost(
    val cost: Money,
    val canBeOverridden: Boolean = true
)

enum class Currency {
    PLN
}
