package pl.exbook.exbook.offer

import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import pl.exbook.exbook.offer.adapter.mongodb.OfferNotFoundException
import pl.exbook.exbook.offer.adapter.rest.NewOfferRequest
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.util.parseMoneyToInt
import java.lang.IllegalArgumentException
import java.util.stream.Collectors

class OfferFacade (
    private val offerRepository: OfferRepository,
    private val userFacade: UserFacade
){
    companion object : KLogging()

    private val MAX_NUMBER_OF_OFFERS_PER_PAGE = 100

    fun getAllOffers(): MutableList<Offer> {
        return offerRepository.findAll()
            .stream()
            .collect(Collectors.toList())
    }

    fun getOffers(offersPerPage: Int?, page: Int?, sorting: String?): Page<Offer> {
        val pageRequest = parsePageRequest(offersPerPage, page, sorting)
        return offerRepository.findAll(pageRequest)
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

    fun getOffer(offerId: OfferId) = offerRepository.findById(offerId) ?: throw OfferNotFoundException(offerId)

    fun addOffer(request: NewOfferRequest, token: UsernamePasswordAuthenticationToken): Offer {
        // Getting user from database that sent
        val user: User = userFacade.getUserByUsername(token.name)

        if (request.cost != null) {
            if (parseMoneyToInt(request.cost.value) < 0) {
                throw IllegalArgumentException("Offer price cannot be below 0.00")
            }
        }

        val offer =  offerRepository.save(request, user.id!!)

        logger.debug("User with id = ${user.id} added new offer with id = ${offer.id}")

        return offer
    }
}
