package pl.exbook.exbook.shipping.domain

class ShippingMethod(
    val id: ShippingMethodId,
    val methodName: String,
    val defaultCost: Cost
)

class ShippingMethodId(val raw: String) {
    companion object {
        fun of(value: String): ShippingMethodId = ShippingMethodId(value)
    }
}

data class Cost(
    val value: Int,
    val currency: Currency,
    val canBeOverridden: Boolean = true
)

enum class Currency {
    PLN
}
