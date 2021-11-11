package pl.exbook.exbook.basket.adapter.mongodb

import java.lang.RuntimeException
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.BasketRepository
import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDocument

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
    items = this.items.map { it.toDocument() },
    totalOffersCost = this.totalOffersCost().toDocument()
)

private fun Basket.Item.toDocument() = ItemDocument(
    offerId = this.offerId.raw,
    quantity = this.quantity,
    offerPrice = this.offerPrice.toDocument()
)

private fun BasketDocument.toDomain() = Basket(
    id = BasketId(this.id),
    userId = UserId(this.userId),
    items = this.items.map { it.toDomain() }.toMutableList()
)

private fun ItemDocument.toDomain() = Basket.Item(
    offerId = OfferId(this.offerId),
    quantity = this.quantity,
    offerPrice = this.offerPrice.toDomain()
)

class BasketNotFoundException() : RuntimeException()
