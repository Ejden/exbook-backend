package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.offer.adapter.rest.OfferDto
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.user.User

class Offer (
    var id: String?,
    var book: Book?,
    var images: Images,
    var description: String?,
    var type: Type,
    var seller: User?,
    var price: Int = 0,
    var location: String,
    var categories: Collection<Category>?,
    var shippingMethods: Collection<ShippingMethod>
) {

    fun toOfferDto() : OfferDto {
        return OfferDto(
            id = this.id!!,
            book = this.book!!,
            images = this.images,
            description = this.description,
            seller = OfferDto.Seller(seller?.id!!, seller?.login!!, seller?.grade!!),
            type = type,
            price = price,
            location = location,
            shippingMethods = shippingMethods,
            categories = categories?.map { category -> category.id!! }!!
        )
    }

    enum class Type {
        EXCHANGE_AND_BUY, EXCHANGE_ONLY, BUY_ONLY
    }

}

class Book(
    var author: String,
    var title: String,
    var isbn: Long?,
    var condition: Condition
)


class Images(
    var thumbnail: Image?,
    val otherImages: Collection<Image>
)

enum class Condition {
    NEW, PERFECT, LIGHTLY_USED, MODERATELY_USED, BAD
}
