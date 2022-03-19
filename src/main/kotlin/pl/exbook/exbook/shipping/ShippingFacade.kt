package pl.exbook.exbook.shipping

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.shipping.domain.ShippingCalculator
import pl.exbook.exbook.shipping.domain.ShippingRepository
import pl.exbook.exbook.shipping.domain.ShippingValidator
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade

class ShippingFacade(
    private val shippingMethodFacade: ShippingMethodFacade,
    private val shippingValidator: ShippingValidator,
    private val shippingCalculator: ShippingCalculator,
    private val shippingRepository: ShippingRepository
) {
    fun calculateSelectedShipping(command: CalculateSelectedShippingCommand): Shipping {
        val shippingMethod = shippingMethodFacade.getShippingMethodById(command.shippingMethodId)
        shippingValidator.validate(shippingMethod, command)
        return shippingCalculator.calculateSelectedShipping(shippingMethod, command)
    }

    fun save(shipping: Shipping): Shipping {
        return shippingRepository.save(shipping)
    }

    fun findShipping(shippingId: ShippingId): Shipping {
        return shippingRepository.findById(shippingId)
    }

    fun remove(shippingId: ShippingId) = shippingRepository.remove(shippingId)
}

data class CalculateSelectedShippingCommand(
    val shippingMethodId: ShippingMethodId,
    val orderItems: List<OrderItem>,
    val shippingAddress: ShippingAddress?,
    val pickupPoint: PickupPoint?,
    val offersShippingMethods: Map<OfferId, List<Offer.ShippingMethod>>
) {
    data class OrderItem(
        val offerId: OfferId,
        val quantity: Long
    )

    data class ShippingAddress(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val address: String,
        val postalCode: String,
        val city: String,
        val country: String
    )

    data class PickupPoint(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val pickupPointId: PickupPointId
    )
}
