package pl.exbook.exbook.listing

import org.springframework.data.domain.Page
import pl.exbook.exbook.common.Cost
import pl.exbook.exbook.common.Currency
import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

class ListingFacade(
    private val offerFacade: OfferFacade,
    private val userFacade: UserFacade,
    private val shippingFacade: ShippingFacade
) {

    fun getOfferListing(offersPerPage: Int?, page: Int?, sorting: String?): Page<DetailedOffer> {
        return offerFacade.getOffers(offersPerPage, page, sorting).map {
            val seller = userFacade.getUserById(it.seller.id.raw)
            val shippingMethods = it.shippingMethods
                .map { s -> Pair(shippingFacade.getShippingMethodById(s.id.raw), s) }
                .map { s -> s.first.toDetailed(s.second.cost) }
            it.toDetailedOffer(seller, shippingMethods)
        }
    }
}

private fun Offer.toDetailedOffer(seller: User, shippingMethods: List<DetailedOffer.ShippingMethod>) = DetailedOffer(
    id = this.id,
    book = this.book.toDetailed(),
    images = this.images.toDetailed(),
    description = this.description,
    type = this.type,
    seller = seller.toDetailed(),
    cost = this.cost,
    location = this.location,
    categories = this.categories.map { it.toDetailed() },
    shippingMethods = shippingMethods
)

private fun Offer.Book.toDetailed() = DetailedOffer.Book(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition
)

private fun User.toDetailed() = DetailedOffer.Seller(
    id = DetailedOffer.SellerId(this.id.raw),
    username = this.login,
    grade = this.grade
)

private fun ShippingMethod.toDetailed(customisedCost: Cost) = DetailedOffer.ShippingMethod(
    id = DetailedOffer.ShippingMethodId(this.id.raw),
    name = this.methodName,
    cost = customisedCost
)

private fun Offer.Images.toDetailed() = DetailedOffer.Images(
    thumbnail = this.thumbnail?.toDetailed(),
    otherImages = this.otherImages.map { it.toDetailed() }
)

private fun Offer.Image.toDetailed() = DetailedOffer.Image(this.url)

private fun Offer.Category.toDetailed() = DetailedOffer.Category(DetailedOffer.CategoryId(this.id.raw))
