package pl.exbook.exbook.offerrecommendations

import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offerrecommendations.domain.OfferDecorator
import pl.exbook.exbook.offerrecommendations.domain.OfferRecommendation
import pl.exbook.exbook.user.UserFacade

@Service
class OfferRecommendationsFacade(
    private val userFacade: UserFacade,
    private val offerFacade: OfferFacade,
    private val offerDecorator: OfferDecorator
) {
    fun getGeneralRecommendedOffers(): List<OfferRecommendation> {
        return offerFacade.getOffers(10, 1, null).content.map {
            val seller = userFacade.getUserById(it.seller.id)
            offerDecorator.decorateOffer(it, seller)
        }
    }
}
