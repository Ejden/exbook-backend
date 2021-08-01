package pl.exbook.exbook.offer

import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import pl.exbook.exbook.offer.adapter.mongodb.BookDocument
import pl.exbook.exbook.offer.adapter.mongodb.CategoryDocument
import pl.exbook.exbook.offer.adapter.mongodb.CostDocument
import pl.exbook.exbook.offer.adapter.mongodb.ImagesDocument
import pl.exbook.exbook.offer.adapter.mongodb.OfferDocument
import pl.exbook.exbook.offer.adapter.mongodb.OfferNotFoundException
import pl.exbook.exbook.offer.adapter.mongodb.OfferRepository
import pl.exbook.exbook.offer.adapter.mongodb.SellerDocument
import pl.exbook.exbook.offer.adapter.mongodb.ShippingMethodDocument
import pl.exbook.exbook.offer.adapter.mongodb.toDomain
import pl.exbook.exbook.offer.adapter.rest.NewOfferRequest
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserId
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
            .map(OfferDocument::toDomain)
            .collect(Collectors.toList())
    }

    fun getOffers(offersPerPage: Int?, page: Int?, sorting: String?): Page<Offer> {
        val pageRequest = parsePageRequest(offersPerPage, page, sorting)
        return offerRepository.findAll(pageRequest).map { it.toDomain() }
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

    fun getOffer(offerId: Offer.OfferId) = offerRepository.findById(offerId.raw).orElseThrow { OfferNotFoundException() }.toDomain()

    fun addOffer(request: NewOfferRequest, token: UsernamePasswordAuthenticationToken): Offer {
        // Getting user from database that sent
        val user: User = userFacade.getUserByUsername(token.name)

        if (request.cost != null) {
            if (parseMoneyToInt(request.cost.value) < 0) {
                logger.warn { "User with id ${user.id.raw}" }
                throw IllegalArgumentException("Offer price cannot be below 0.00")
            }
        }

        val offer =  offerRepository.save(request.toDocument(user.id))

        logger.debug("User with id = ${user.id} added new offer with id = ${offer.id}")

        return offer.toDomain()
    }
}

private fun NewOfferRequest.toDocument(userId: UserId) = OfferDocument(
    book = BookDocument(
        author = this.book.author,
        title = this.book.title,
        isbn = this.book.isbn,
        condition = this.book.condition
    ),
    images = ImagesDocument(
        thumbnail = null,
        otherImages = emptyList()
    ),
    description = this.description,
    seller = SellerDocument(userId.raw),
    type = this.type,
    cost = if (this.cost == null) null else CostDocument(
        value = parseMoneyToInt(this.cost.value),
        currency = this.cost.currency
    ),
    location = this.location,
    categories = this.categories.map { CategoryDocument(it.id) },
    shippingMethods = this.shippingMethods.map { ShippingMethodDocument(
        id = it.id,
        cost = CostDocument(
            value = parseMoneyToInt(it.cost.value),
            currency = it.cost.currency
        )
    )}
)
