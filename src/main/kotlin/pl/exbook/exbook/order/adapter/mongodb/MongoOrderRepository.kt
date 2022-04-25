package pl.exbook.exbook.order.adapter.mongodb

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.PagingAndSortingRepository
import pl.exbook.exbook.shared.dto.MoneyDocument
import pl.exbook.exbook.shared.dto.MoneyDto
import java.time.Instant
import org.springframework.stereotype.Repository

@Repository
interface MongoOrderRepository : PagingAndSortingRepository<OrderDocument, String> {
    fun findAllByBuyerId(buyerId: String, pageable: Pageable): Page<OrderDocument>

    fun findAllBySellerId(sellerId: String, pageable: Pageable): Page<OrderDocument>

    fun removeById(orderId: String)
}

@Document(collection = "orders")
data class OrderDocument(
    val id: String,
    val buyerId: String,
    val sellerId: String,
    val shippingId: String,
    val items: List<OrderItemDocument>,
    val orderType: String,
    val exchangeBooks: List<ExchangeBookDocument>,
    val orderDate: Instant,
    val status: String,
    val totalCost: MoneyDocument,
    val note: String
)

data class OrderItemDocument(
    val offerId: String,
    val quantity: Long,
    val cost: MoneyDto?
)

data class ExchangeBookDocument(
    val id: String,
    val author: String,
    val title: String,
    val isbn: String?,
    val condition: String,
    val quantity: Int
)
