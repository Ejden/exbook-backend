package pl.exbook.exbook.offer

import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.adapter.mongodb.OfferNotFoundException
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.offer.domain.CreateOfferCommand
import pl.exbook.exbook.offer.domain.OfferCreator
import pl.exbook.exbook.offer.domain.OfferVersionNotFoundException
import pl.exbook.exbook.offer.domain.OfferVersioningRepository
import pl.exbook.exbook.offer.domain.UpdateOfferCommand
import pl.exbook.exbook.security.domain.UnauthorizedException
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.user.UserFacade

@Service
class OfferFacade (
    private val offerRepository: OfferRepository,
    private val offerVersioningRepository: OfferVersioningRepository,
    private val offerCreator: OfferCreator,
    private val userFacade: UserFacade
){
    private val MAX_NUMBER_OF_OFFERS_PER_PAGE = 100

    fun getOffers(offersPerPage: Int?, page: Int?, sorting: String?): Page<Offer> {
        val pageRequest = parsePageRequest(offersPerPage, page, sorting)
        return offerRepository.findAll(pageRequest)
    }

    fun getOffer(offerId: OfferId) = offerRepository.findById(offerId) ?: throw OfferNotFoundException(offerId)

    fun getOfferVersion(
        offerId: OfferId,
        version: Instant
    ): Offer = offerVersioningRepository.getOfferVersion(offerId, version) ?: throw OfferVersionNotFoundException(offerId, version)

    fun getOfferVersion(
        offerVersionId: OfferVersionId
    ): Offer = offerVersioningRepository.getOfferVersion(offerVersionId) ?: throw OfferVersionNotFoundException(offerVersionId)

    fun addOffer(
        request: CreateOfferCommand,
        token: UsernamePasswordAuthenticationToken
    ): Offer = offerCreator.addOffer(request, token)

    fun updateOffer(command: UpdateOfferCommand, token: UsernamePasswordAuthenticationToken): Offer {
        val user = userFacade.getUserByUsername(token.name)
        val offer = offerRepository.findById(command.offerId) ?: throw OfferNotFoundException(command.offerId)
        if (user.id != offer.seller.id) {
            throw UnauthorizedException()
        }

        return offerCreator.updateOffer(command, offer)
    }

    private fun parsePageRequest(_offersPerPage: Int?, _page: Int?, _sorting: String?): Pageable {
        var offersPerPage = _offersPerPage
        var page = _page
        val sorting = _sorting

        if (offersPerPage == null) offersPerPage = 50
        if (offersPerPage > MAX_NUMBER_OF_OFFERS_PER_PAGE) offersPerPage = MAX_NUMBER_OF_OFFERS_PER_PAGE

        if (page == null) page = 0

        if (sorting == null)
            return PageRequest.of(page, offersPerPage)

        return PageRequest.of(page, offersPerPage, parseSorting(sorting))
    }

    /**
    sorting:
    - p for price
    - d for date of publication
    each is followed by
    - d for descending
    - a for ascending
     **/
    private fun parseSorting(sorting: String): Sort {
        val sort = when (sorting.first()) {
            'p' -> Sort.by("price")
            else -> Sort.by("publicationDate")
        }

        if (sorting.last() == 'a') {
            sort.ascending()
        } else {
            sort.descending()
        }

        return sort
    }
}
