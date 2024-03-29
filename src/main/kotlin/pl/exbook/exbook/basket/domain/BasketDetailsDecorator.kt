package pl.exbook.exbook.basket.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.domain.User

@Service
class BasketDetailsDecorator {
    fun decorateBasketWithDetails(basket: Basket, sellers: Map<UserId, User>, offers: Map<OfferId, Offer>): DetailedBasket {
        val detailedItemsGroups = mutableListOf<DetailedBasket.ItemGroup>()

        basket.itemsGroups.forEach {
            val seller = sellers[it.key.sellerId]!!
            val items = it.value.items.map { item ->
                val offer = offers[item.offer.id]!!
                DetailedBasket.Item(
                    offer = DetailedBasket.Offer(
                        id = offer.id,
                        price = when(it.key.orderType) {
                            Order.OrderType.BUY -> offer.price
                            Order.OrderType.EXCHANGE -> Money.zeroPln()
                        },
                        book = DetailedBasket.Book(
                            author = offer.book.author,
                            title = offer.book.title
                        ),
                        images = DetailedBasket.Images(
                            thumbnail = offer.images.thumbnail?.let { img -> DetailedBasket.Image(img.url) },
                            allImages = offer.images.allImages.map { img -> DetailedBasket.Image(img.url) }
                        ),
                        seller = DetailedBasket.Seller(
                            id = offer.seller.id,
                            firstName = seller.firstName,
                            lastName = seller.lastName
                        )
                    ),
                    quantity = item.quantity
                )
            }
            val exchangeBooks = it.value.exchangeBooks.map { book ->
                DetailedBasket.ExchangeBook(
                    id = book.id,
                    author = book.author,
                    title = book.title,
                    isbn = book.isbn,
                    condition = book.condition,
                    quantity = book.quantity
                )
            }

            detailedItemsGroups.add(DetailedBasket.ItemGroup(
                seller = DetailedBasket.Seller(
                    id = seller.id,
                    firstName = seller.firstName,
                    lastName = seller.lastName
                ),
                orderType = it.key.orderType,
                items = items,
                exchangeBooks = exchangeBooks
            ))
        }

        return DetailedBasket(
            id = basket.id,
            userId = basket.userId,
            itemsGroups = detailedItemsGroups
        )
    }
}
