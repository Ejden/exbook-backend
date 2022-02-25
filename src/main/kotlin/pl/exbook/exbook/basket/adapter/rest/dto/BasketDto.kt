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
        val items: List<ItemDto>
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

    companion object {
        fun fromDomain(basket: Basket) = BasketDto(
            id = basket.id.raw,
            buyer = BuyerDto(basket.userId.raw),
            itemsGroups = basket.itemsGroups.entries.map { (groupKey, items) ->
                ItemsGroupDto(
                    seller = SellerDto(groupKey.sellerId.raw),
                    orderType = groupKey.orderType.name,
                    items = items.map { ItemDto(OfferDto(it.offer.id.raw), it.quantity) }
                )
            }
        )
    }
}
