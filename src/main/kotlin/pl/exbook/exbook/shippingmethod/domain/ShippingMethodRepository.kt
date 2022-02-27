package pl.exbook.exbook.shippingmethod.domain

import pl.exbook.exbook.shared.ShippingMethodId

interface ShippingMethodRepository {

    fun findById(shippingMethodId: ShippingMethodId): ShippingMethod?

    fun findByName(name: String): ShippingMethod?

    fun findAll(): List<ShippingMethod>

    fun save(shippingMethod: ShippingMethod): ShippingMethod
}
