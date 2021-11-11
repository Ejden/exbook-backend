package pl.exbook.exbook.basket.domain

import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

open class Basket(
    val id: BasketId,
    val userId: UserId,
    val items: MutableList<Item>
) {

    open class Item(
        val offerId: OfferId,
        val offerPrice: Money,
        var quantity: Long
    )

    fun addToBasket(offerId: OfferId, offerPrice: Money, quantity: Long) {
        items.add(Item(offerId, offerPrice, quantity))
    }

    fun removeFromBasket(offerId: OfferId) {
        items.removeIf { it.offerId == offerId }
    }

    fun changeItemQuantity(offerId: OfferId, newQuantity: Long) {
        items.find { it.offerId == offerId }?.quantity = newQuantity
    }

    fun totalOffersCost(): Money = items
        .map { it.offerPrice * it.quantity }
        .fold(Money.zero(Currency.PLN)) { sum, arg2 -> sum + arg2 }

    fun toDetailed(items: List<DetailedBasket.Item>): DetailedBasket {
        return DetailedBasket(
            id = this.id,
            userId = this.userId,
            items = items
        )
    }
}

class DetailedBasket(
    id: BasketId,
    userId: UserId,
    items: List<Item>
): Basket(id, userId, items.toMutableList()) {

    class Item(
        val offer: Offer,
        quantity: Long
    ): Basket.Item(offer.id, offer.price, quantity)

    class Offer(
        val id: OfferId,
        val price: Money,
        val book: Book,
        val images: Images,
        val seller: Seller
    )

    class Book(
        val author: String,
        val title: String
    )

    class Seller(
        val id: UserId,
        val firstName: String,
        val lastName: String
    )

    data class Images(
        val thumbnail: Image?,
        val otherImages: Collection<Image>
    )

    data class Image(val url: String)
}
