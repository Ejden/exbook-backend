package pl.exbook.exbook.offer

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
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
    var sellerId: String
) {

    fun toOffer() : Offer {
        return Offer(id, book, images, description, User(sellerId))
    }

}
