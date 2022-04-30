package pl.exbook.exbook.order.adapter.mongodb

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.OrderRepository
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDocument
import pl.exbook.exbook.shared.dto.toDomain
import pl.exbook.exbook.shared.dto.toDto
import java.lang.RuntimeException
import pl.exbook.exbook.shared.ExchangeBookId

class DatabaseOrderRepository(private val mongoOrderRepository: MongoOrderRepository) : OrderRepository {
    override fun findById(id: OrderId): Order = mongoOrderRepository.findById(id.raw)
        .orElseThrow { OrderNotFoundException(id) }
        .toDomain()

    override fun save(order: Order): Order = mongoOrderRepository.save(order.toDocument()).toDomain()

    override fun findByBuyerId(buyerId: UserId, itemsPerPage: Int?, page: Int?, sorting: String?): Page<Order> =
        mongoOrderRepository.findAllByBuyerId(buyerId.raw, createPageable(itemsPerPage, page, sorting))
            .map { it.toDomain() }

    override fun findByBuyerIdAndStatus(
        buyerId: UserId,
        status: List<Order.OrderStatus>,
        itemsPerPage: Int?,
        page: Int?,
        sorting: String?
    ): Page<Order> = mongoOrderRepository.findAllByBuyerIdAndStatusIn(
        buyerId.raw,
        status.map { it.name },
        createPageable(itemsPerPage, page, sorting)
    ).map { it.toDomain() }

    override fun findBySellerId(sellerId: UserId, itemsPerPage: Int?, page: Int?, sorting: String?): Page<Order> =
        mongoOrderRepository.findAllBySellerId(sellerId.raw, createPageable(itemsPerPage, page, sorting))
            .map { it.toDomain() }

    override fun findBySellerIdAndStatus(
        sellerId: UserId,
        status: List<Order.OrderStatus>,
        itemsPerPage: Int?,
        page: Int?,
        sorting: String?
    ): Page<Order> = mongoOrderRepository.findAllBySellerIdAndStatusIn(
        sellerId.raw,
        status.map { it.name },
        createPageable(itemsPerPage, page, sorting)
    ).map { it.toDomain() }

    override fun remove(orderId: OrderId) = mongoOrderRepository.removeById(orderId.raw)

    private fun createPageable(itemsPerPage: Int?, page: Int?, sorting: String?): Pageable {
        return PageRequest.of(page?.minus(1) ?: 0, itemsPerPage ?: 10, Sort.Direction.DESC, "orderDate")
    }
}

private fun OrderDocument.toDomain() = Order(
    id = OrderId(this.id),
    buyer = Order.Buyer(UserId(this.buyerId)),
    seller = Order.Seller(UserId(this.sellerId)),
    shipping = Order.Shipping(ShippingId(this.shippingId)),
    items = this.items.map { it.toDomain() },
    orderType = Order.OrderType.valueOf(this.orderType),
    exchangeBooks = this.exchangeBooks.map { it.toDomain() },
    orderDate = this.orderDate,
    status = Order.OrderStatus.valueOf(this.status),
    totalCost = this.totalCost.toDomain(),
    note = this.note
)

private fun OrderItemDocument.toDomain() = Order.OrderItem(
    offerId = OfferId(this.offerId),
    quantity = this.quantity,
    cost = this.cost?.toDomain()
)

private fun ExchangeBookDocument.toDomain() = Order.ExchangeBook(
    id = ExchangeBookId(this.id),
    author = this.author,
    title = this.title,
    condition = Offer.Condition.valueOf(this.condition),
    isbn = this.isbn,
    quantity = this.quantity
)

private fun Order.toDocument() = OrderDocument(
    id = this.id.raw,
    buyerId = this.buyer.id.raw,
    sellerId = this.seller.id.raw,
    shippingId = this.shipping.id.raw,
    items = this.items.map { it.toDocument() },
    orderType = this.orderType.name,
    exchangeBooks = this.exchangeBooks.map { it.toDocument() },
    orderDate = this.orderDate,
    status = this.status.name,
    totalCost = this.totalCost.toDocument(),
    note = this.note
)

private fun Order.OrderItem.toDocument() = OrderItemDocument(
    offerId = this.offerId.raw,
    quantity = this.quantity,
    cost = this.cost?.toDto()
)

private fun Order.ExchangeBook.toDocument() = ExchangeBookDocument(
    id = this.id.raw,
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name,
    quantity = this.quantity
)

data class OrderNotFoundException(val orderId: OrderId) : RuntimeException("Order with id $orderId not found")
