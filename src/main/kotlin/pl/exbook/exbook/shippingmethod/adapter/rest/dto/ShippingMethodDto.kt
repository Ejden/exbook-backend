package pl.exbook.exbook.shippingmethod.adapter.rest.dto

import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto
import pl.exbook.exbook.shippingmethod.domain.Cost
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod

data class ShippingMethodDto(
    val id: String,
    val name: String,
    val pickupPointMethod: Boolean,
    val defaultCost: CostDto
) {
    data class CostDto(
        val cost: MoneyDto,
        val canBeOverridden: Boolean
    )

    companion object {
        fun fromDomain(shippingMethod: ShippingMethod) = ShippingMethodDto(
            id = shippingMethod.id.raw,
            name = shippingMethod.methodName,
            pickupPointMethod = shippingMethod.pickupPointMethod,
            defaultCost = shippingMethod.defaultCost.toDto()
        )
    }
}

private fun Cost.toDto() = ShippingMethodDto.CostDto(
    cost = this.cost.toDto(),
    canBeOverridden = this.canBeOverridden
)
