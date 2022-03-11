package pl.exbook.exbook.ability

import pl.exbook.exbook.adapters.InMemoryShippingMethodRepository
import pl.exbook.exbook.pln
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.domain.NewShippingMethodCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodCreator

class ShippingMethodDomainAbility {
    private val shippingMethodRepository: InMemoryShippingMethodRepository = InMemoryShippingMethodRepository()
    private val shippingMethodCreator: ShippingMethodCreator = ShippingMethodCreator(shippingMethodRepository)
    val facade: ShippingMethodFacade = ShippingMethodFacade(shippingMethodRepository, shippingMethodCreator)

    fun thereIsShippingMethod(name: String): ShippingMethod {
        return facade.addShippingMethod(
            NewShippingMethodCommand(
                name = name,
                pickupPointMethod = false,
                cost = NewShippingMethodCommand.Cost(
                    defaultCost = "8.99".pln(),
                    canBeOverridden = true
                )
            )
        )
    }
}
