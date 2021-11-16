package pl.exbook.exbook.shippingmethod.domain

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shippingmethod.adapter.rest.NewShippingMethod

interface ShippingMethodRepository {

    fun findById(shippingMethodId: ShippingMethodId): ShippingMethod?

    fun findAll(): List<ShippingMethod>

    fun save(newShippingMethod: NewShippingMethod): ShippingMethod
}
