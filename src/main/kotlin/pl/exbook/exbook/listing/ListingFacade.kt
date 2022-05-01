package pl.exbook.exbook.listing

import java.math.BigDecimal
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import pl.exbook.exbook.listing.domain.DetailedOffer
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.stock.StockFacade
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

@Service
class ListingFacade(
    private val offerFacade: OfferFacade,
    private val userFacade: UserFacade,
    private val shippingMethodFacade: ShippingMethodFacade,
    private val stockFacade: StockFacade
) {
    fun getOfferListing(
        searchingPhrase: String,
        bookConditions: List<Offer.Condition>?,
        offerType: List<Offer.Type>?,
        priceFrom: BigDecimal?,
        priceTo: BigDecimal?,
        location: String?,
        categoryId: CategoryId?,
        offersPerPage: Int?,
        page: Int?,
        sorting: String?
    ): Page<DetailedOffer> {
        return offerFacade.getOffers(
            searchingPhrase = searchingPhrase,
            bookConditions = bookConditions,
            offerType = offerType,
            priceFrom = priceFrom,
            priceTo = priceTo,
            location = location,
            categoryId = categoryId,
            offersPerPage = offersPerPage,
            page = page,
            sorting = sorting
        ).map {
            val seller = userFacade.getUserById(it.seller.id)
            val shippingMethods = it.shippingMethods
                .map { s -> Pair(shippingMethodFacade.getShippingMethodById(s.id), s) }
                .map { s -> s.first.toDetailed(s.second.price) }
            val stock = stockFacade.getStock(it.stockId)
            it.toDetailedOffer(seller, shippingMethods, findCheapestShippingMethod(shippingMethods), stock.inStock)
        }
    }

    fun getUserOffers(
        username: String,
        offersPerPage: Int?,
        page: Int?,
        sorting: String?
    ): Page<DetailedOffer> {
        val seller = userFacade.getUserByUsername(username)
        return offerFacade.getUserOffers(seller.id, offersPerPage, page, sorting).map {
            val shippingMethods = it.shippingMethods
                .map { s -> Pair(shippingMethodFacade.getShippingMethodById(s.id), s) }
                .map { s -> s.first.toDetailed(s.second.price) }
            val stock = stockFacade.getStock(it.stockId)
            it.toDetailedOffer(seller, shippingMethods, findCheapestShippingMethod(shippingMethods), stock.inStock)
        }
    }

    fun getOffer(offerId: OfferId): DetailedOffer {
        val offer = offerFacade.getOffer(offerId)
        val seller = userFacade.getUserById(offer.seller.id)
        val shippingMethods = offer.shippingMethods
            .map { s -> Pair(shippingMethodFacade.getShippingMethodById(s.id), s) }
            .map { s -> s.first.toDetailed(s.second.price) }
        val stock = stockFacade.getStock(offer.stockId)

        return offer.toDetailedOffer(seller, shippingMethods, findCheapestShippingMethod(shippingMethods), stock.inStock)
    }

    private fun findCheapestShippingMethod(shippingMethods: List<DetailedOffer.ShippingMethod>): DetailedOffer.ShippingMethod {
        return when {
            shippingMethods.size == 1 && shippingMethods[0].name == "Odbiór osobisty" -> shippingMethods[0]
            else -> shippingMethods.filterNot { it.name == "Odbiór osobisty" }.minByOrNull { it.price }!!
        }
    }
}

private fun Offer.toDetailedOffer(
    seller: User,
    shippingMethods: List<DetailedOffer.ShippingMethod>,
    cheapestMethod: DetailedOffer.ShippingMethod,
    inStock: Long
) = DetailedOffer(
    id = this.id,
    book = this.book.toDetailed(),
    images = this.images.toDetailed(),
    description = this.description,
    type = this.type,
    seller = seller.toDetailed(),
    price = this.price,
    location = this.location,
    category = this.category.toDetailed(),
    shipping = DetailedOffer.Shipping(
        shippingMethods = shippingMethods,
        cheapestMethod = cheapestMethod
    ),
    inStock = inStock
)

private fun Offer.Book.toDetailed() = DetailedOffer.Book(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition
)

private fun User.toDetailed() = DetailedOffer.Seller(
    id = UserId(this.id.raw),
    username = this.username,
    grade = this.grade
)

private fun ShippingMethod.toDetailed(customisedMoney: Money) = DetailedOffer.ShippingMethod(
    id = ShippingMethodId(this.id.raw),
    name = this.methodName,
    price = customisedMoney
)

private fun Offer.Images.toDetailed() = DetailedOffer.Images(
    thumbnail = this.thumbnail?.toDetailed(),
    allImages = this.allImages.map { it.toDetailed() }
)

private fun Offer.Image.toDetailed() = DetailedOffer.Image(this.url)

private fun Offer.Category.toDetailed() = DetailedOffer.Category(CategoryId(this.id.raw))
