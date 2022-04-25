package pl.exbook.exbook.basket.domain

import pl.exbook.exbook.offer.domain.Offer.Condition
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

data class Basket(
    val id: BasketId,
    val userId: UserId,
    val itemsGroups: MutableMap<ItemsGroupKey, ItemsGroup>
) {
    data class Item(
        val offer: Offer,
        var quantity: Long
    ): Comparable<Item> {
        constructor(offerId: OfferId, quantity: Long): this(Offer(offerId), quantity)

        fun changeQuantity(quantity: Long) {
            this.quantity = quantity
        }

        override fun compareTo(other: Item): Int {
            return offer.id.raw.compareTo(other.offer.id.raw)
        }
    }

    data class Offer(val id: OfferId)

    data class ItemsGroupKey(
        val sellerId: UserId,
        val orderType: Order.OrderType
    )

    data class ItemsGroup(
        val sellerId: UserId,
        val orderType: Order.OrderType,
        val exchangeBooks: MutableList<ExchangeBook>,
        val items: List<Item>
    ) {
        fun addItem(item: Item): ItemsGroup = this.copy(items = this.items + item)

        fun removeItem(offerId: OfferId) = this.copy(items = this.items.filterNot { it.offer.id == offerId })
    }

    data class ExchangeBook(
        val id: ExchangeBookId,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Condition,
        val quantity: Int
    )

    data class Seller(
        val id: UserId
    )

    fun addToBasket(offerId: OfferId, sellerId: UserId, orderType: Order.OrderType, quantity: Long) {
        val key = ItemsGroupKey(sellerId, orderType)
        val itemsGroup = itemsGroups[key] ?: ItemsGroup(sellerId, orderType, mutableListOf(), emptyList())
        val item = itemsGroup.items.find { it.offer.id == offerId }

        if (item == null) {
            this.itemsGroups[key] = itemsGroup.addItem(Item(offerId, quantity))
        } else {
           item.changeQuantity(item.quantity + quantity)
        }
    }

    fun removeFromBasket(offerId: OfferId, orderType: Order.OrderType) {
        itemsGroups.entries
            .firstOrNull { it.key.orderType == orderType && it.value.items.any { item -> item.offer.id == offerId } }
            ?.let {
                val newItemGroup = it.value.items.filterNot { item -> item.offer.id == offerId }
                if (newItemGroup.isEmpty()) {
                    this.itemsGroups.remove(it.key)
                } else {
                    this.itemsGroups[it.key] = it.value.removeItem(offerId)
                }
            }
    }

    fun changeItemQuantity(offerId: OfferId, newQuantity: Long, sellerId: UserId, orderType: Order.OrderType) {
        if (newQuantity == 0L) {
            removeFromBasket(offerId, orderType)
            return
        }

        val itemGroup = itemsGroups.entries
            .firstOrNull { it.key.orderType == orderType && it.value.items.any { item -> item.offer.id == offerId} }

        if (itemGroup == null) {
            addToBasket(offerId, sellerId, orderType, newQuantity)
        } else {
            val item = itemGroup.value.items.find { it.offer.id == offerId }

            if (item == null) {
                addToBasket(offerId, sellerId, orderType, newQuantity)
            } else {
                item.changeQuantity(newQuantity)
            }
        }
    }

    fun addExchangeBook(sellerId: UserId, book: ExchangeBook) {
        val key = ItemsGroupKey(sellerId, Order.OrderType.EXCHANGE)
        val itemGroup = itemsGroups[key]
            ?: throw BasketValidationException(
                "Cannot find matching group for exchange book. seller: ${sellerId.raw}, basket: ${this.id.raw}"
            )

        itemGroup.exchangeBooks.add(book)
    }

    fun removeExchangeBook(sellerId: UserId, exchangeBookId: ExchangeBookId) {
        val key = ItemsGroupKey(sellerId, Order.OrderType.EXCHANGE)
        itemsGroups[key]?.exchangeBooks?.removeIf { it.id == exchangeBookId }
    }

    fun removeGroups(groups: List<ItemsGroupKey>) {
        groups.forEach { itemsGroups.remove(it) }
    }
}

data class DetailedBasket(
    val id: BasketId,
    val userId: UserId,
    val itemsGroups: List<ItemGroup>,
) {
    val totalOffersCost: Money = itemsGroups.foldRight(Money.zeroPln()) { itemGroup, acc -> itemGroup.groupTotalOffersPrice + acc }

    data class ItemGroup(
        val seller: Seller,
        val orderType: Order.OrderType,
        val items: List<Item>,
        val exchangeBooks: List<ExchangeBook>
    ) {
        val groupTotalOffersPrice: Money = this.items.foldRight(Money.zeroPln()) { item, acc ->
            item.totalPrice + acc
        }
    }

    data class Item(
        val offer: Offer,
        val quantity: Long,
    ) {
        val totalPrice: Money = this.offer.price?.times(this.quantity) ?: Money.zeroPln()
    }

    data class ExchangeBook(
        val id: ExchangeBookId,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Condition,
        val quantity: Int
    )

    data class Offer(
        val id: OfferId,
        val price: Money?,
        val book: Book,
        val images: Images,
        val seller: Seller
    )

    data class Book(
        val author: String,
        val title: String
    )

    data class Seller(
        val id: UserId,
        val firstName: String,
        val lastName: String
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Image(val url: String)
}
