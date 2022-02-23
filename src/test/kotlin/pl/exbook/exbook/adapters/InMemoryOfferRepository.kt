package pl.exbook.exbook.adapters

import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.shared.OfferId

class InMemoryOfferRepository : OfferRepository {
    private val memory = mutableListOf<Offer>()

    override fun findById(offerId: OfferId): Offer? {
        return memory.firstOrNull { it.id == offerId }
    }

    override fun getOfferVersionFrom(offerId: OfferId, timestamp: Instant): Offer? {
        return memory.firstOrNull { it.id == offerId && timestamp >= it.versionCreationDate && timestamp < it.versionExpireDate  }
    }

    override fun findAll(pageable: Pageable): Page<Offer> {
        return Page.empty(pageable)
    }

    override fun findAll(): List<Offer> {
        return memory.toList()
    }

    override fun save(offer: Offer): Offer {
        memory.add(offer)
        return offer
    }
}
