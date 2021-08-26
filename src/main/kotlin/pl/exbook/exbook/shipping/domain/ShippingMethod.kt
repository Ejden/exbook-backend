package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.ShippingMethodId

class ShippingMethod(
    val id: ShippingMethodId,
    val methodName: String,
    val defaultCost: Cost
)

data class Cost(
    val value: Int,
    val currency: Currency,
    val canBeOverridden: Boolean = true
)

enum class Currency {
    PLN
}
