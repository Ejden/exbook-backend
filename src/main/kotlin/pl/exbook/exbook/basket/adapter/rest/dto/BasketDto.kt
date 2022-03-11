package pl.exbook.exbook.basket.adapter.rest.dto

import pl.exbook.exbook.basket.domain.Basket

data class BasketDto(
    val id: String,
    val buyer: BuyerDto,
    val itemsGroups: List<ItemsGroupDto>,
) {
    data class BuyerDto(
        val id: String
    )

    data class ItemsGroupDto(
        val seller: SellerDto,
        val orderType: String,
        val items: List<ItemDto>,
        val exchangeBooks: List<ExchangeBook>
    )

    data class SellerDto(
        val id: String
    )

    data class ItemDto(
        val offer: OfferDto,
        val quantity: Long
    )

    data class OfferDto(
        val id: String
    )

    data class ExchangeBook(
        val id: String,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String,
        val quantity: Int
    )

    companion object {
        fun fromDomain(basket: Basket) = BasketDto(
            id = basket.id.raw,
            buyer = BuyerDto(basket.userId.raw),
            itemsGroups = basket.itemsGroups.entries.map { (groupKey, group) ->
                ItemsGroupDto(
                    seller = SellerDto(groupKey.sellerId.raw),
                    orderType = groupKey.orderType.name,
                    items = group.items.map { ItemDto(OfferDto(it.offer.id.raw), it.quantity) },
                    exchangeBooks = group.exchangeBooks.map { it.toDto() }
                )
            }
        )
    }
}

private fun Basket.ExchangeBook.toDto() = BasketDto.ExchangeBook(
    id = this.id.raw,
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name,
    quantity = this.quantity
)
