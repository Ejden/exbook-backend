package pl.exbook.exbook.mock

import io.mockk.every
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.basket.adapter.mongodb.BasketNotFoundException
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.TestData.sampleAuthor
import pl.exbook.exbook.shared.TestData.sampleBasketId
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleExchangeBookId
import pl.exbook.exbook.shared.TestData.sampleIsbn
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleTitle
import pl.exbook.exbook.shared.TestData.sampleUserId
import pl.exbook.exbook.shared.UserId

class BasketFacadeMocks(private val basketFacade: BasketFacade) {
    fun thereIsBasket(init: BasketBuilder.() -> Unit) {
        val builder = BasketBuilder().apply(init)
        val mockBasket = builder.build()
        every { basketFacade.getUserBasket(mockBasket.userId) } returns mockBasket
        every { basketFacade.getUserBasket(builder.username) } returns mockBasket
    }

    fun thereIsNoBasket(userId: UserId, username: String) {
        every { basketFacade.getUserBasket(userId) } throws BasketNotFoundException()
        every { basketFacade.getUserBasket(username) } throws BasketNotFoundException()
    }
}

class BasketBuilder {
    var id: BasketId = sampleBasketId
    var buyerId: UserId = sampleUserId
    var username: String = sampleBuyerUsername
    var itemsGroups: MutableMap<Basket.ItemsGroupKey, Basket.ItemsGroup> = mutableMapOf()

    fun build() = Basket(
        id = id,
        userId = buyerId,
        itemsGroups = itemsGroups
    )

    class ItemsGroupBuilder {
        var sellerId: UserId = sampleSellerId
        var orderType: OrderType = OrderType.BUY
        var exchangeBooks: MutableList<Basket.ExchangeBook> = mutableListOf()
        var items: MutableList<Basket.Item> = mutableListOf()

        fun build() = Basket.ItemsGroup(
            sellerId = sellerId,
            orderType = orderType,
            exchangeBooks = exchangeBooks,
            items = items
        )

        fun exchangeBook(init: ExchangeBookBuilder.() -> Unit) {
            exchangeBooks += ExchangeBookBuilder().apply(init).build()
        }

        fun item(init: ItemBuilder.() -> Unit) {
            items += ItemBuilder().apply(init).build()
        }
    }

    class ExchangeBookBuilder {
        var id: ExchangeBookId = sampleExchangeBookId
        var author: String = sampleAuthor
        var title: String = sampleTitle
        var isbn: String = sampleIsbn
        var condition: Offer.Condition = Offer.Condition.PERFECT
        var quantity: Int = 1

        fun build() = Basket.ExchangeBook(id, author, title, isbn, condition, quantity)
    }

    class ItemBuilder {
        var offerId: OfferId = sampleOfferId
        var quantity: Long = 1

        fun build() = Basket.Item(Basket.Offer(offerId), quantity)
    }

    fun itemsGroup(init: ItemsGroupBuilder.() -> Unit) {
        val itemsGroup = ItemsGroupBuilder().apply(init).build()
        itemsGroups[Basket.ItemsGroupKey(itemsGroup.sellerId, itemsGroup.orderType)] = itemsGroup
    }
}
