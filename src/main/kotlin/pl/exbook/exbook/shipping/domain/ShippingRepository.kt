package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shared.ShippingId

interface ShippingRepository {

    fun findById(shippingId: ShippingId): Shipping

    fun save(shipping: Shipping): Shipping

    fun remove(shippingId: ShippingId)
}
