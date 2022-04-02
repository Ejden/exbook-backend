package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import java.lang.RuntimeException

class ShippingCalculator(
    private val offerFacade: OfferFacade,
    private val shippingFactory: ShippingFactory
) {
    fun calculateSelectedShipping(
        selectedShippingMethod: ShippingMethod, request: CalculateSelectedShippingCommand
    ): Shipping {
        validateSelectedShippingMethod(selectedShippingMethod.id, request.orderItems.map { it.offerId })
        val cost = calculateShippingCost(selectedShippingMethod.id, request)
        return shippingFactory.createShipping(selectedShippingMethod, request, cost)
    }

    fun previewShippingMethodForPurchase(
        command: PreviewAvailableShippingCommand, shippingMethods: List<ShippingMethod>
    ): AvailableShipping {
        return AvailableShipping(command.orders.mapValues {
            it.value.commonShippingMethods().mapNotNull { option ->
                shippingMethods.firstOrNull { shippingMethod -> shippingMethod.id == option.id }
                    ?.toOption(option.price)
            }.toList()
        }.mapKeys { AvailableShipping.OrderKey(it.key.sellerId, it.key.orderType) })
    }

    private fun PreviewAvailableShippingCommand.Order.commonShippingMethods(): List<Offer.ShippingMethod> {
        val shippingMethods = this.offers.flatMap { it.shippingMethods }
        val commonShippingMethods = this.offers
            .fold(offers.getOrNull(0)?.shippingMethods?.map { it.id } ?: emptySet()) { acc, x ->
                acc.intersect(x.shippingMethods.map { it.id }.toSet())
            }

        return commonShippingMethods.map {
            Offer.ShippingMethod(
                it,
                shippingMethods
                    .filter { shippingMethod -> shippingMethod.id == it }
                    .maxOf { shippingMethod -> shippingMethod.price }
            )
        }
    }

    private fun ShippingMethod.toOption(cost: Money) = AvailableShipping.ShippingOption(
        methodId = this.id,
        methodName = this.methodName,
        pickupPoint = pickupPointMethod,
        price = cost
    )

    private fun calculateShippingCost(
        shippingMethodId: ShippingMethodId, request: CalculateSelectedShippingCommand
    ): Shipping.Cost {
        val maxOfferDeliveryCostInOrder = request.offersShippingMethods.flatMap { it.value }.maxOf { it.price }
        return Shipping.Cost(maxOfferDeliveryCostInOrder)
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
