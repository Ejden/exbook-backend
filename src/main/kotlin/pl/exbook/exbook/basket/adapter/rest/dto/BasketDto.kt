package pl.exbook.exbook.basket.adapter.rest.dto

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
}
