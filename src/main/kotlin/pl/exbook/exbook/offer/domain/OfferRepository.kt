package pl.exbook.exbook.offer.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.exbook.exbook.offer.adapter.rest.NewOfferRequest
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

interface OfferRepository {

    fun findById(offerId: OfferId): Offer?

    fun findAll(pageable: Pageable): Page<Offer>

    fun findAll(): List<Offer>

    fun save(newOfferRequest: NewOfferRequest, userId: UserId): Offer
}
