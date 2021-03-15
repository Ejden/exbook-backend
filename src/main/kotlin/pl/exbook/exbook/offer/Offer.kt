package pl.exbook.exbook.offer

import org.springframework.data.mongodb.core.mapping.Document
import pl.exbook.exbook.category.Category
import pl.exbook.exbook.shipping.ShippingMethod
import pl.exbook.exbook.user.User

@Document(collation = "offers")
class Offer (
    var id: String?,
    var book: Book?,
    var images: Images,
    var description: String?,
    var type: Type,
    var seller: User?,
    var price: Int?,
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
            seller = OfferDto.Seller(seller?.id!!),
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

class Image(val url: String)

enum class Condition {
    NEW, PERFECT, LIGHTLY_USED, MODERATELY_USED, BAD
}
