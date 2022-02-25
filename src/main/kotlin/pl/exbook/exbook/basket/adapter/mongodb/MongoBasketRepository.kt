package pl.exbook.exbook.basket.adapter.mongodb

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoBasketRepository : MongoRepository<BasketDocument, String> {

    fun getByUserId(userId: String): BasketDocument?
}

@Document(collection = "baskets")
data class BasketDocument(
    val id: String,
    val userId: String,
    val itemsGroups: List<ItemsGroupDocument>,
)

data class ItemsGroupDocument(
    val sellerId: String,
    val orderType: String,
    val items: List<ItemDocument>
)

data class ItemDocument(
    val offerId: String,
    val quantity: Long,
)
