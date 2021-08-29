package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.ShippingMethodId
import java.math.BigDecimal

class ShippingMethod(
    val id: ShippingMethodId,
    val methodName: String,
    val defaultCost: Cost
)

data class Cost(
    val amount: BigDecimal,
    val currency: Currency,
    val canBeOverridden: Boolean = true
)

enum class Currency {
    PLN
}
