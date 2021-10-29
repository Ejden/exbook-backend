package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shipping.CalculateSelectedShippingRequest
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import java.lang.RuntimeException

class ShippingCalculator(
    private val offerFacade: OfferFacade,
    private val shippingFactory: ShippingFactory
) {

    fun calculateSelectedShipping(selectedShippingMethod: ShippingMethod, request: CalculateSelectedShippingRequest): Shipping {
        validateSelectedShippingMethod(selectedShippingMethod.id, request.orderItems.map { it.offerId })
        return shippingFactory.createShipping(selectedShippingMethod, request)
    }

    private fun validateSelectedShippingMethod(selectedShippingMethod: ShippingMethodId, offerIds: List<OfferId>) {
        val offers = offerIds.map { offerFacade.getOffer(it) }
        if (!offers.all { it.shippingMethodIds().contains(selectedShippingMethod) }) {
            throw CommonShippingMethodNotFoundException(offerIds, selectedShippingMethod)
        }
    }

    private fun Offer.shippingMethodIds() = this.shippingMethods.map { shippingMethod -> shippingMethod.id }
}

class CommonShippingMethodNotFoundException(
    offerIds: List<OfferId>,
    shippingMethodId: ShippingMethodId
) : RuntimeException("Offers with ids $offerIds don't have common shipping method with id $shippingMethodId")
