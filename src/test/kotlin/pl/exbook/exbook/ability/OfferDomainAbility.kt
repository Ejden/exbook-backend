package pl.exbook.exbook.ability

import io.mockk.mockk
import pl.exbook.exbook.adapters.InMemoryOfferRepository
import pl.exbook.exbook.adapters.InMemoryOfferVersioningRepository
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.OfferCreator
import pl.exbook.exbook.offer.domain.OfferVersioningService
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.stock.StockFacade
import pl.exbook.exbook.stock.domain.OfferValidator
import pl.exbook.exbook.user.UserFacade

class OfferDomainAbility {
    private val offerRepository: InMemoryOfferRepository = InMemoryOfferRepository()
    private val offerVersioningRepository: InMemoryOfferVersioningRepository = InMemoryOfferVersioningRepository()
    private val offerVersioningService: OfferVersioningService = OfferVersioningService(offerVersioningRepository)
    private val userFacade: UserFacade = mockk()
    private val stockFacade: StockFacade = mockk()
    private val shippingMethodFacade: ShippingMethodFacade = mockk()
    private val offerValidator: OfferValidator = OfferValidator(shippingMethodFacade)
    private val offerCreator: OfferCreator = OfferCreator(
        offerRepository = offerRepository,
        offerVersioningService = offerVersioningService,
        userFacade = userFacade,
        stockFacade = stockFacade,
        offerValidator = offerValidator
    )
    val facade: OfferFacade = OfferFacade(
        offerRepository = offerRepository,
        offerVersioningRepository = offerVersioningRepository,
        offerCreator = offerCreator,
        userFacade = userFacade
    )
}
