package pl.exbook.exbook.shippingmethod.domain

import java.util.UUID
import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.ShippingMethodId

@Service
class ShippingMethodCreator(private val shippingMethodRepository: ShippingMethodRepository) {
    fun createShippingMethod(command: NewShippingMethodCommand): ShippingMethod {
        if (shippingMethodRepository.findByName(command.name) != null) {
            throw ShippingMethodAlreadyExistException(command.name)
        }

        val shippingMethod = ShippingMethod(
            id = ShippingMethodId(UUID.randomUUID().toString()),
            methodName = command.name,
            pickupPointMethod = command.pickupPointMethod,
            defaultCost = Cost(
                cost = command.cost.defaultCost,
                canBeOverridden = command.cost.canBeOverridden
            )
        )

        return shippingMethodRepository.save(shippingMethod)
    }
}
