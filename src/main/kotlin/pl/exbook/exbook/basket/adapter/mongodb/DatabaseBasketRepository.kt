package pl.exbook.exbook.basket.adapter.mongodb

import org.springframework.stereotype.Component
import java.lang.RuntimeException
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.BasketRepository
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

@Component
class DatabaseBasketRepository(
    private val mongoBasketRepository: MongoBasketRepository
) : BasketRepository {

    override fun getUserBasket(userId: UserId): Basket {
        return mongoBasketRepository.getByUserId(userId.raw)?.toDomain() ?: throw BasketNotFoundException()
    }

    override fun save(basket: Basket): Basket {
        return mongoBasketRepository.save(basket.toDocument()).toDomain()
    }
}

private fun Basket.toDocument() = BasketDocument(
    id = this.id.raw,
    userId = this.userId.raw,
    itemsGroups = this.itemsGroups.entries.map { (key, group) ->
        ItemsGroupDocument(
            sellerId = key.sellerId.raw,
            orderType = key.orderType.name,
            items = group.items.map { it.toDocument() },
            exchangeBooks = group.exchangeBooks.map { it.toDocument() }
        )
    }
)

private fun Basket.Item.toDocument() = ItemDocument(
    offerId = this.offer.id.raw,
    quantity = this.quantity,
)

private fun Basket.ExchangeBook.toDocument() = ExchangeBookDocument(
    id = this.id.raw,
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name,
    quantity = this.quantity
)

private fun BasketDocument.toDomain() = Basket(
    id = BasketId(this.id),
    userId = UserId(this.userId),
    itemsGroups = this.itemsGroups.associate {
        Basket.ItemsGroupKey(
            sellerId = UserId(it.sellerId),
            orderType = Order.OrderType.valueOf(it.orderType)
        ) to Basket.ItemsGroup(
            sellerId = UserId(it.sellerId),
            orderType = Order.OrderType.valueOf(it.orderType),
            items = it.items.map { item -> item.toDomain() },
            exchangeBooks = it.exchangeBooks.map { book -> book.toDomain() }.toMutableList()
        )
    }.toMutableMap()

)

private fun ItemDocument.toDomain() = Basket.Item(
    offerId = OfferId(this.offerId),
    quantity = this.quantity,
)

private fun ExchangeBookDocument.toDomain() = Basket.ExchangeBook(
    id = ExchangeBookId(this.id),
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = Offer.Condition.valueOf(this.condition),
    quantity = this.quantity
)

class BasketNotFoundException : NotFoundException()
