package pl.exbook.exbook.adapters

import java.math.BigDecimal
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

class InMemoryOfferRepository : OfferRepository {
    private val memory = mutableListOf<Offer>()

    override fun findById(offerId: OfferId): Offer? {
        return memory.firstOrNull { it.id == offerId }
    }

    override fun findBySellerId(sellerId: UserId, pageable: Pageable): Page<Offer> {
        return PageImpl(memory.filter { it.seller.id == sellerId })
    }

    override fun findAll(pageable: Pageable): Page<Offer> {
        return PageImpl(memory)
    }

    override fun findAll(): List<Offer> {
        return memory.toList()
    }

    override fun save(offer: Offer): Offer {
        memory.add(offer)
        return offer
    }

    override fun findWithFilters(
        searchingPhrases: List<String>,
        bookConditions: List<Offer.Condition>,
        offerType: List<Offer.Type>,
        priceFrom: BigDecimal?,
        priceTo: BigDecimal?,
        location: String?,
        categoryId: CategoryId?,
        pageable: Pageable
    ): Page<Offer> {
        return PageImpl(memory)
    }
}
