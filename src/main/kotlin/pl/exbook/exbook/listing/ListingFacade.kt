package pl.exbook.exbook.listing

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.*
import pl.exbook.exbook.event.events.OfferViewEvent
import pl.exbook.exbook.shipping.ShippingMethodFacade
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

class ListingFacade(
    private val offerFacade: OfferFacade,
    private val userFacade: UserFacade,
    private val shippingMethodFacade: ShippingMethodFacade,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun getOfferListing(offersPerPage: Int?, page: Int?, sorting: String?): Page<DetailedOffer> {
        return offerFacade.getOffers(offersPerPage, page, sorting).map {
            val seller = userFacade.getUserById(it.seller.id)
            val shippingMethods = it.shippingMethods
                .map { s -> Pair(shippingMethodFacade.getShippingMethodById(s.id), s) }
                .map { s -> s.first.toDetailed(s.second.money) }
            it.toDetailedOffer(seller, shippingMethods, findCheapestShippingMethod(shippingMethods))
        }
    }

    fun getOffer(offerId: OfferId): DetailedOffer {
        val offer = offerFacade.getOffer(offerId)
        val seller = userFacade.getUserById(offer.seller.id)
        val shippingMethods = offer.shippingMethods
            .map { s -> Pair(shippingMethodFacade.getShippingMethodById(s.id), s) }
            .map { s -> s.first.toDetailed(s.second.money) }

        applicationEventPublisher.publishEvent(
            OfferViewEvent(
                source = this,
                offerId = offerId,
                sellerId = offer.seller.id,
                viewerId = null
            )
        )

        return offer.toDetailedOffer(seller, shippingMethods, findCheapestShippingMethod(shippingMethods))
    }

    private fun findCheapestShippingMethod(shippingMethods: List<DetailedOffer.ShippingMethod>): DetailedOffer.ShippingMethod {
        return when {
            shippingMethods.size == 1 && shippingMethods[0].name == "Odbiór osobisty" -> shippingMethods[0]
            else -> shippingMethods.filterNot { it.name == "Odbiór osobisty" }.minByOrNull { it.money }!!
        }
    }
}

private fun Offer.toDetailedOffer(
    seller: User,
    shippingMethods: List<DetailedOffer.ShippingMethod>,
    cheapestMethod: DetailedOffer.ShippingMethod
) = DetailedOffer(
    id = this.id,
    book = this.book.toDetailed(),
    images = this.images.toDetailed(),
    description = this.description,
    type = this.type,
    seller = seller.toDetailed(),
    money = this.price,
    location = this.location,
    category = this.category.toDetailed(),
    shipping = DetailedOffer.Shipping(
        shippingMethods = shippingMethods,
        cheapestMethod = cheapestMethod
    )
)

private fun Offer.Book.toDetailed() = DetailedOffer.Book(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition
)

private fun User.toDetailed() = DetailedOffer.Seller(
    id = UserId(this.id!!.raw),
    username = this.login,
    grade = this.grade
)

private fun ShippingMethod.toDetailed(customisedMoney: Money) = DetailedOffer.ShippingMethod(
    id = ShippingMethodId(this.id.raw),
    name = this.methodName,
    money = customisedMoney
)

private fun Offer.Images.toDetailed() = DetailedOffer.Images(
    thumbnail = this.thumbnail?.toDetailed(),
    otherImages = this.otherImages.map { it.toDetailed() }
)

private fun Offer.Image.toDetailed() = DetailedOffer.Image(this.url)

private fun Offer.Category.toDetailed() = DetailedOffer.Category(CategoryId(this.id.raw))
