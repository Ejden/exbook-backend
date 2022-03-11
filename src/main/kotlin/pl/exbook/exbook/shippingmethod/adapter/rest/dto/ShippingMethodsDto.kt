package pl.exbook.exbook.shippingmethod.adapter.rest.dto

import pl.exbook.exbook.shippingmethod.domain.ShippingMethod

data class ShippingMethodsDto(
    val shippingMethods: List<ShippingMethodDto>
) {
    companion object {
        fun fromDomain(shippingMethods: List<ShippingMethod>) = ShippingMethodsDto(
            shippingMethods = shippingMethods.map { ShippingMethodDto.fromDomain(it) }
        )
    }
}
