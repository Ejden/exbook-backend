package pl.exbook.exbook.adapters

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodRepository

class InMemoryShippingMethodRepository : ShippingMethodRepository {
    private val memory = mutableMapOf<ShippingMethodId, ShippingMethod>()

    override fun findById(shippingMethodId: ShippingMethodId): ShippingMethod? = memory[shippingMethodId]

    override fun findByName(name: String): ShippingMethod? = memory.values.firstOrNull { it.methodName == name }

    override fun findAll(): List<ShippingMethod> = memory.values.toList()

    override fun save(shippingMethod: ShippingMethod): ShippingMethod {
        memory[shippingMethod.id] = shippingMethod
        return memory[shippingMethod.id]!!
    }
}
