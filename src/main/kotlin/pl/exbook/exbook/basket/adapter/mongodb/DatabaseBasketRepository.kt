package pl.exbook.exbook.basket.adapter.mongodb

import java.lang.RuntimeException
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.BasketRepository
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

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
    itemsGroups = this.itemsGroups.entries.map { (key, items) ->
        ItemsGroupDocument(
            sellerId = key.sellerId.raw,
            orderType = key.orderType.name,
            items = items.map { it.toDocument() }
        )
    }
)

private fun Basket.Item.toDocument() = ItemDocument(
    offerId = this.offer.id.raw,
    quantity = this.quantity,
)

private fun BasketDocument.toDomain() = Basket(
    id = BasketId(this.id),
    userId = UserId(this.userId),
    itemsGroups = this.itemsGroups.associate {
        Basket.ItemsGroupKey(
            sellerId = UserId(it.sellerId),
            orderType = Order.OrderType.valueOf(it.orderType)
        ) to it.items.map { item -> item.toDomain() }
    }.toMutableMap()

)

private fun ItemDocument.toDomain() = Basket.Item(
    offerId = OfferId(this.offerId),
    quantity = this.quantity,
)

class BasketNotFoundException : RuntimeException()
