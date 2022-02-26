package pl.exbook.exbook.basket.adapter.rest.dto

import pl.exbook.exbook.basket.domain.DetailedBasket
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto

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
        val otherImages: Collection<ImageDto>
    )

    data class ImageDto(val url: String)

    data class SellerDto(
        val id: String,
        val firstName: String,
        val lastName: String
    )

    companion object {
        fun fromDomain(detailedBasket: DetailedBasket) = DetailedBasketDto(
            id = detailedBasket.id.raw,
            buyer = BuyerDto(detailedBasket.userId.raw),
            itemsGroups = detailedBasket.itemsGroups.map { it.toDto() },
            totalOffersCost = detailedBasket.totalOffersCost.toDto()
        )
    }
}

private fun DetailedBasket.ItemGroup.toDto() = DetailedBasketDto.ItemsGroupDto(
    seller = DetailedBasketDto.SellerDto(
        id = this.seller.id.raw,
        firstName = this.seller.firstName,
        lastName = this.seller.lastName
    ),
    orderType = this.orderType.name,
    items = this.items.map { it.toDto() },
    groupTotalOffersPrice = this.groupTotalOffersPrice.toDto()
)

private fun DetailedBasket.Item.toDto() = DetailedBasketDto.ItemDto(
    offer = DetailedBasketDto.OfferDto(
        id = this.offer.id.raw,
        price = this.offer.price?.toDto(),
        book = this.offer.book.toDto(),
        images = this.offer.images.toDto(),
        seller = this.offer.seller.toDto()
    ),
    quantity = this.quantity,
    totalPrice = this.totalPrice.toDto()
)

private fun DetailedBasket.Book.toDto() = DetailedBasketDto.BookDto(
    author = this.author,
    title = this.title
)

private fun DetailedBasket.Images.toDto() = DetailedBasketDto.ImagesDto(
    thumbnail = this.thumbnail?.let { DetailedBasketDto.ImageDto(it.url) },
    otherImages = this.allImages.map { DetailedBasketDto.ImageDto(it.url) }
)

private fun DetailedBasket.Seller.toDto() = DetailedBasketDto.SellerDto(
    id = this.id.raw,
    firstName = this.firstName,
    lastName = this.lastName
)
