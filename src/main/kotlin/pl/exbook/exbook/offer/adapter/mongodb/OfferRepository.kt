package pl.exbook.exbook.offer.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.offer.domain.Book
import pl.exbook.exbook.offer.domain.Images
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.user.User

interface OfferRepository : MongoRepository<OfferDatabaseModel, String> {

}

@Document(collection = "offers")
data class OfferDatabaseModel(
    @Id
    var id: String?,
    var book: Book?,
    var images: Images,
    var description: String?,
    var sellerId: String,
    var type: Offer.Type,
    var price: Int?,
    var location: String,
    var categories: Collection<String>,
    var shippingMethods: Collection<ShippingMethod>
) {

    fun toOffer() : Offer {
        return Offer(
            id = id,
            book = book,
            images = images,
            description = description,
            type = type,
            seller = User(sellerId),
            price = price!!,
            location = location,
            categories = categories.map{ id -> Category(id = id, name = null, image = null) },
            shippingMethods = shippingMethods
        )
    }

    fun toOffer(seller: User): Offer {
        return Offer(
            id = id,
            book = book,
            images = images,
            description = description,
            type = type,
            seller = seller,
            price = price!!,
            location = location,
            categories = categories.map{ id -> Category(id = id, name = null, image = null) },
            shippingMethods = shippingMethods
        )
    }

}

class OfferNotFoundException: RuntimeException()
