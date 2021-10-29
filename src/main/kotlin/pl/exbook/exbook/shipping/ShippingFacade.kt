package pl.exbook.exbook.shipping

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.PickupPointId
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

    fun calculateSelectedShipping(request: CalculateSelectedShippingRequest): Shipping {
        val shippingMethod = shippingMethodFacade.getShippingMethodById(request.shippingMethodId)
        shippingValidator.validate(shippingMethod, request)
        return shippingCalculator.calculateSelectedShipping(shippingMethod, request)
    }

    fun save(shipping: Shipping): Shipping {
        return shippingRepository.save(shipping)
    }
}

data class CalculateSelectedShippingRequest(
    val shippingMethodId: ShippingMethodId,
    val orderItems: List<OrderItem>,
    val shippingAddress: ShippingAddress?,
    val pickupPoint: PickupPoint?
) {
    data class OrderItem(
        val offerId: OfferId,
        val orderType: Order.OrderType,
        val exchangeBook: ExchangeBook?,
        val quantity: Int
    )

    data class ExchangeBook(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
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
