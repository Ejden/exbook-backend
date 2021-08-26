package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shipping.adapter.rest.NewShippingMethod

interface ShippingMethodRepository {

    fun findById(shippingMethodId: ShippingMethodId): ShippingMethod?

    fun findAll(): List<ShippingMethod>

    fun save(newShippingMethod: NewShippingMethod): ShippingMethod
}
