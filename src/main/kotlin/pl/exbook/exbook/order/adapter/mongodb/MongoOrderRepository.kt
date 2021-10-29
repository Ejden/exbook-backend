package pl.exbook.exbook.order.adapter.mongodb

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.Repository
import pl.exbook.exbook.shared.dto.MoneyDto
import java.time.Instant

interface MongoOrderRepository : Repository<OrderDocument, String> {

    fun findById(id: String): OrderDocument?

    fun save(orderDocument: OrderDocument): OrderDocument
}

@Document(collation = "orders")
data class OrderDocument(
    val id: String?,
    val buyer: BuyerDocument,
    val items: List<OrderItemDocument>,
    val orderDate: Instant,
    val returned: Boolean,
    val accepted: Boolean,
    val shippingId: String
)

data class BuyerDocument(
    val id: String
)

data class OrderItemDocument(
    val offerId: String,
    val seller: SellerDocument,
    val orderType: String,
    val exchangeBook: ExchangeBookDocument?,
    val quantity: Int,
    val price: MoneyDto?
)

data class ExchangeBookDocument(
    val author: String,
    val title: String,
    val isbn: Long?,
    val condition: String
)

data class SellerDocument(
    val id: String
)
