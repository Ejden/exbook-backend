package pl.exbook.exbook.adapters

import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shipping.adapter.mongodb.ShippingNotFoundException
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.shipping.domain.ShippingRepository

class InMemoryShippingRepository : ShippingRepository {
    private val memory = mutableMapOf<ShippingId, Shipping>()

    override fun findById(shippingId: ShippingId): Shipping = memory[shippingId]
        ?: throw ShippingNotFoundException(shippingId)

    override fun save(shipping: Shipping): Shipping {
        memory[shipping.id] = shipping
        return memory[shipping.id]!!
    }

    override fun remove(shippingId: ShippingId) {
        memory.remove(shippingId)
    }
}
