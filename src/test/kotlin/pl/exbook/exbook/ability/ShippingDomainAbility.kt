package pl.exbook.exbook.ability

import io.mockk.mockk
import pl.exbook.exbook.adapters.InMemoryShippingRepository
import pl.exbook.exbook.mock.OfferBuilder
import pl.exbook.exbook.mock.OfferFacadeMocks
import pl.exbook.exbook.mock.ShippingMethodBuilder
import pl.exbook.exbook.mock.ShippingMethodFacadeMocks
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.ShippingCalculator
import pl.exbook.exbook.shipping.domain.ShippingFactory
import pl.exbook.exbook.shipping.domain.ShippingValidator
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade

class ShippingDomainAbility {
    private val shippingMethodFacade: ShippingMethodFacade = mockk()
    private val shippingValidator = ShippingValidator()
    private val offerFacade: OfferFacade = mockk()
    private val shippingFactory = ShippingFactory()
    private val shippingCalculator = ShippingCalculator(offerFacade, shippingFactory)
    private val shippingRepository = InMemoryShippingRepository()

    private val shippingMethodFacadeMocks = ShippingMethodFacadeMocks(shippingMethodFacade)
    private val offerFacadeMocks = OfferFacadeMocks(offerFacade)

    val facade = ShippingFacade(
        shippingMethodFacade = shippingMethodFacade,
        shippingValidator = shippingValidator,
        shippingCalculator = shippingCalculator,
        shippingRepository = shippingRepository
    )

    fun thereIsShippingMethod(init: ShippingMethodBuilder.() -> Unit) =
        shippingMethodFacadeMocks.thereIsShippingMethod(init)
    fun thereIsOffer(init: OfferBuilder.() -> Unit) = offerFacadeMocks.thereIsOffer(init)
    fun thereIsOffer(offer: Offer) = offerFacadeMocks.thereIsOffer(offer)
    fun thereAreOffers(vararg offer: Offer) = offer.forEach { thereIsOffer(it) }
    fun thereIsNoOffer(offerId: OfferId) = offerFacadeMocks.thereIsNoOfferFor(offerId)
}
