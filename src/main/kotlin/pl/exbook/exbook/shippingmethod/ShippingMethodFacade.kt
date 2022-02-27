package pl.exbook.exbook.shippingmethod

import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shippingmethod.domain.NewShippingMethodCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodCreator
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodNotFoundException
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodRepository

@Service
class ShippingMethodFacade(
    private val shippingRepository: ShippingMethodRepository,
    private val shippingMethodCreator: ShippingMethodCreator
) {
    fun getShippingMethods(): List<ShippingMethod> = shippingRepository.findAll()

    fun getShippingMethod(
        shippingMethodId: ShippingMethodId
    ): ShippingMethod? = shippingRepository.findById(shippingMethodId)

    fun getShippingMethodById(
        shippingMethodId: ShippingMethodId
    ): ShippingMethod = shippingRepository.findById(shippingMethodId) ?: throw ShippingMethodNotFoundException(shippingMethodId)

    fun addShippingMethod(
        command: NewShippingMethodCommand
    ): ShippingMethod = shippingMethodCreator.createShippingMethod(command)
}
