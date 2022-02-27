package pl.exbook.exbook.shippingmethod.domain

import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.Money

data class NewShippingMethodCommand(
    val name: String,
    val pickupPointMethod: Boolean,
    val cost: Cost
) {
    init {
        if (name.isEmpty()) {
            throw IllegalParameterException("Shipping method name cannot be empty")
        }
        if (cost.defaultCost < Money.zero(cost.defaultCost.currency)) {
            throw IllegalParameterException("Shipping method default cost cannot be less than 0.00")
        }
    }

    data class Cost(
        val defaultCost: Money,
        val canBeOverridden: Boolean
    )
}
