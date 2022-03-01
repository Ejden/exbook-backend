package pl.exbook.exbook.offer.domain

import java.time.Instant
import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId

@Service
class OfferVersioningService(private val repository: OfferVersioningRepository) {
    fun getOfferVersion(offerId: OfferId, timestamp: Instant): Offer? = repository.getOfferVersion(offerId, timestamp)

    fun getOfferVersion(offerVersionId: OfferVersionId): Offer? = repository.getOfferVersion(offerVersionId)

    fun getActiveOfferVersion(offerId: OfferId): Offer? = repository.getActiveOfferVersion(offerId)

    // TODO: Add transaction
    fun saveNewOfferVersion(newOfferVersion: Offer): Offer {
        val activeOfferVersion = repository.getActiveOfferVersion(newOfferVersion.id)

        return if (activeOfferVersion != null) {
            val deactivatedOfferVersion = activeOfferVersion.deactivate(newOfferVersion.versionCreationDate)
            try {
                repository.saveOfferVersion(deactivatedOfferVersion)
                repository.insertNewVersion(newOfferVersion)
            } catch (cause: Exception) {
                repository.saveOfferVersion(activeOfferVersion)
                throw cause
            }
        } else {
            repository.insertNewVersion(newOfferVersion)
        }
    }
}
