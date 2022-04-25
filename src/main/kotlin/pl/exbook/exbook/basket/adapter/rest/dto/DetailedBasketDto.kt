package pl.exbook.exbook.basket.adapter.rest.dto

import pl.exbook.exbook.shared.dto.MoneyDto

data class DetailedBasketDto(
    val id: String,
    val buyer: BuyerDto,
    val itemsGroups: List<ItemsGroupDto>,
    val totalOffersCost: MoneyDto
) {
    data class BuyerDto(val id: String)

    data class ItemsGroupDto(
        val seller: SellerDto,
        val orderType: String,
        val items: List<ItemDto>,
        val exchangeBooks: List<ExchangeBook>,
        val groupTotalOffersPrice: MoneyDto
    )

    data class ItemDto(
        val offer: OfferDto,
        val quantity: Long,
        val totalPrice: MoneyDto
    )

    data class OfferDto(
        val id: String,
        val price: MoneyDto?,
        val book: BookDto,
        val images: ImagesDto,
        val seller: SellerDto
    )

    data class BookDto(
        val author: String,
        val title: String
    )

    data class ImagesDto(
        val thumbnail: ImageDto?,
        val allImages: Collection<ImageDto>
    )

    data class ImageDto(val url: String)

    data class SellerDto(
        val id: String,
        val firstName: String,
        val lastName: String
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
