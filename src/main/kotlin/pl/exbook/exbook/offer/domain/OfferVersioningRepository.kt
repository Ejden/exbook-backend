package pl.exbook.exbook.offer.domain

import java.time.Instant
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId

interface OfferVersioningRepository {
    fun getOfferVersion(offerId: OfferId, timestamp: Instant): Offer?

    fun getOfferVersion(offerVersionId: OfferVersionId): Offer?

    fun getActiveOfferVersion(offerId: OfferId): Offer?

    fun saveOfferVersion(offer: Offer): Offer

    fun insertNewVersion(offer: Offer): Offer
}
