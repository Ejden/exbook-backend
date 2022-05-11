package pl.exbook.exbook.offer.domain

import java.math.BigDecimal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

interface OfferRepository {
    fun findById(offerId: OfferId): Offer?
    fun findBySellerId(sellerId: UserId, pageable: Pageable): Page<Offer>
    fun findAll(pageable: Pageable): Page<Offer>
    fun findAll(): List<Offer>
    fun save(offer: Offer): Offer
    fun findWithFilters(
        searchingPhrases: List<String>,
        bookConditions: List<Offer.Condition>,
        offerType: List<Offer.Type>,
        priceFrom: BigDecimal?,
        priceTo: BigDecimal?,
        location: String?,
        categoryId: CategoryId?,
        pageable: Pageable
    ): Page<Offer>
}
