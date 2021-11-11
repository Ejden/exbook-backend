package pl.exbook.exbook.basket.adapter.mongodb

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.shared.dto.MoneyDocument

interface MongoBasketRepository : MongoRepository<BasketDocument, String> {

    fun getByUserId(userId: String): BasketDocument?
}

@Document(collection = "baskets")
data class BasketDocument(
    val id: String,
    val userId: String,
    val items: List<ItemDocument>,
    val totalOffersCost: MoneyDocument
)

data class ItemDocument(
    val offerId: String,
    val quantity: Long,
    val offerPrice: MoneyDocument
)
