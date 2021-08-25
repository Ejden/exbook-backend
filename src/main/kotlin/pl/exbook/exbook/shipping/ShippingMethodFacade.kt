package pl.exbook.exbook.shipping

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shipping.adapter.rest.NewShippingMethod
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.shipping.domain.ShippingMethodRepository

class ShippingMethodFacade(
    private val shippingRepository: ShippingMethodRepository
) {

    fun getShippingMethods(): List<ShippingMethod> = shippingRepository.findAll()

    fun getShippingMethodById(
        shippingMethodId: ShippingMethodId
    ): ShippingMethod = shippingRepository.findById(shippingMethodId) ?: throw ShippingMethodNotFoundException(shippingMethodId)

    fun addShippingMethod(newShippingMethod: NewShippingMethod): ShippingMethod = shippingRepository.save(newShippingMethod)
}

class ShippingMethodNotFoundException(id: ShippingMethodId): RuntimeException("Shipping method with id ${id.raw} not found")
