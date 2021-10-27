package pl.exbook.exbook.order.adapter.mongodb

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.OrderRepository
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDomain
import pl.exbook.exbook.shared.dto.toDto

class DatabaseOrderRepository(private val mongoOrderRepository: MongoOrderRepository) : OrderRepository {

    override fun findById(id: OrderId): Order? {
        return mongoOrderRepository.findById(id.raw)?.toDomain()
    }

    override fun save(order: Order): Order {
        return mongoOrderRepository.save(order.toDocument()).toDomain()
    }
}

private fun OrderDocument.toDomain() = Order(
    id = OrderId(this.id!!),
    buyer = this.buyer.toDomain(),
    items = this.items.map { it.toDomain() },
    orderDate = this.orderDate,
    returned = this.returned
)

private fun BuyerDocument.toDomain() = Order.Buyer(UserId(this.id))

private fun OrderItemDocument.toDomain() = Order.OrderItem(
    offerId = OfferId(this.offerId),
    seller = Order.Seller(UserId(this.seller.id)),
    orderType = Order.OrderType.valueOf(this.orderType),
    exchangeBook = this.exchangeBook?.toDomain(),
    quantity = this.quantity,
    price = this.price?.toDomain()
)

private fun ExchangeBookDocument.toDomain() = Order.ExchangeBook(
    author = this.author,
    title = this.title,
    condition = Offer.Condition.valueOf(this.condition),
    isbn = this.isbn
)

private fun Order.toDocument() = OrderDocument(
    id = this.id?.raw,
    buyer = BuyerDocument(this.buyer.id.raw),
    items = this.items.map { it.toDocument() },
    orderDate = this.orderDate,
    returned = this.returned
)

private fun Order.OrderItem.toDocument() = OrderItemDocument(
    offerId = this.offerId.raw,
    seller = SellerDocument(this.seller.id.raw),
    orderType = this.orderType.name,
    exchangeBook = exchangeBook?.toDocument(),
    quantity = this.quantity,
    price = this.price?.toDto()
)

private fun Order.ExchangeBook.toDocument() = ExchangeBookDocument(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)
