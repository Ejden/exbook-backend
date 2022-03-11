package pl.exbook.exbook.basket.adapter.mongodb

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
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
    val items: List<ItemDocument>,
    val exchangeBooks: List<ExchangeBookDocument>
)

data class ExchangeBookDocument(
    val id: String,
    val author: String,
    val title: String,
    val isbn: String?,
    val condition: String,
    val quantity: Int
)

data class ItemDocument(
    val offerId: String,
    val quantity: Long,
)
