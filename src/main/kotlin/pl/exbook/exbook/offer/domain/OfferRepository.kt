package pl.exbook.exbook.offer.domain

import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.exbook.exbook.shared.OfferId

interface OfferRepository {

    fun findById(offerId: OfferId): Offer?

    fun getOfferVersionFrom(offerId: OfferId, timestamp: Instant): Offer?

    fun findAll(pageable: Pageable): Page<Offer>

    fun findAll(): List<Offer>

    fun save(offer: Offer): Offer
}
