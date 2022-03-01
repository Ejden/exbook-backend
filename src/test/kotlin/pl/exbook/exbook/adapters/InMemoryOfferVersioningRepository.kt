package pl.exbook.exbook.adapters

import java.time.Instant
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferVersioningRepository
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId

class InMemoryOfferVersioningRepository : OfferVersioningRepository {
    private val memory = mutableMapOf<OfferVersionId, Offer>()

    override fun getOfferVersion(
        offerId: OfferId,
        timestamp: Instant
    ): Offer? = memory.values.firstOrNull {
        it.id == offerId && it.versionCreationDate <= timestamp && (it.versionExpireDate == null || it.versionExpireDate!! > timestamp)
    }

    override fun getOfferVersion(offerVersionId: OfferVersionId): Offer? = memory[offerVersionId]

    override fun getActiveOfferVersion(offerId: OfferId): Offer? = memory.values.firstOrNull { it.id == offerId && it.versionExpireDate == null }

    override fun saveOfferVersion(offer: Offer): Offer {
        memory[offer.versionId] = offer
        return memory[offer.versionId]!!
    }

    override fun insertNewVersion(offer: Offer): Offer {
        if (memory[offer.versionId] != null) {
            throw RuntimeException()
        }

        memory[offer.versionId] = offer
        return memory[offer.versionId]!!
    }
}
