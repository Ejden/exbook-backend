package pl.exbook.exbook.basket.adapter.rest.dto

import pl.exbook.exbook.basket.domain.AddExchangeBookToBasketCommand
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.ChangeItemQuantityCommand
import pl.exbook.exbook.basket.domain.DetailedBasket
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDto
import pl.exbook.exbook.util.mapper.FromDomainMapper
import pl.exbook.exbook.util.mapper.TwoWayMapper

object BasketMapper : FromDomainMapper<Basket, BasketDto> {
    override fun fromDomain(from: Basket): BasketDto = BasketDto(
        id = from.id.raw,
        buyer = BasketDto.BuyerDto(from.userId.raw),
        itemsGroups = from.itemsGroups.entries.map { (groupKey, group) ->
            BasketDto.ItemsGroupDto(
                seller = BasketDto.SellerDto(groupKey.sellerId.raw),
                orderType = groupKey.orderType.name,
                items = group.items.map { BasketDto.ItemDto(BasketDto.OfferDto(it.offer.id.raw), it.quantity) },
                exchangeBooks = group.exchangeBooks.map { it.toDto() }
            )
        }
    )

    private fun Basket.ExchangeBook.toDto() = BasketDto.ExchangeBook(
        id = this.id.raw,
        author = this.author,
        title = this.title,
        isbn = this.isbn,
        condition = this.condition.name,
        quantity = this.quantity
    )
}

object DetailedBasketMapper : FromDomainMapper<DetailedBasket, DetailedBasketDto> {
    override fun fromDomain(from: DetailedBasket): DetailedBasketDto = DetailedBasketDto(
        id = from.id.raw,
        buyer = DetailedBasketDto.BuyerDto(from.userId.raw),
        itemsGroups = from.itemsGroups.map { it.toDto() },
        totalOffersCost = from.totalOffersCost.toDto(),
        canProceedToSummaryPage = from.canProceedToSummaryPage
    )

    private fun DetailedBasket.ItemGroup.toDto() = DetailedBasketDto.ItemsGroupDto(
        seller = DetailedBasketDto.SellerDto(
            id = this.seller.id.raw,
            firstName = this.seller.firstName,
            lastName = this.seller.lastName
        ),
        orderType = this.orderType.name,
        items = this.items.map { it.toDto() },
        exchangeBooks = this.exchangeBooks.map { it.toDto() },
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

    private fun DetailedBasket.ExchangeBook.toDto() = DetailedBasketDto.ExchangeBook(
        id = this.id.raw,
        author = this.author,
        title = this.title,
        isbn = this.isbn,
        condition = this.condition.name,
        quantity = this.quantity
    )

    private fun DetailedBasket.Book.toDto() = DetailedBasketDto.BookDto(
        author = this.author,
        title = this.title
    )

    private fun DetailedBasket.Images.toDto() = DetailedBasketDto.ImagesDto(
        thumbnail = this.thumbnail?.let { DetailedBasketDto.ImageDto(it.url) },
        allImages = this.allImages.map { DetailedBasketDto.ImageDto(it.url) }
    )

    private fun DetailedBasket.Seller.toDto() = DetailedBasketDto.SellerDto(
        id = this.id.raw,
        firstName = this.firstName,
        lastName = this.lastName
    )
}

class AddItemToBasketMapper(val username: String) : TwoWayMapper<AddItemToBasketRequest, AddItemToBasketCommand, Basket, BasketDto> {
    override fun toDomain(from: AddItemToBasketRequest): AddItemToBasketCommand = AddItemToBasketCommand(
        username = username,
        offerId = OfferId(from.offerId),
        quantity = from.quantity,
        orderType = Order.OrderType.valueOf(from.orderType)
    )

    override fun fromDomain(from: Basket): BasketDto = BasketMapper.fromDomain(from)
}

class ChangeItemQuantityMapper(
    val offerId: OfferId,
    val username: String
) : TwoWayMapper<ChangeItemQuantityRequest, ChangeItemQuantityCommand, Basket, BasketDto> {
    override fun toDomain(from: ChangeItemQuantityRequest): ChangeItemQuantityCommand = ChangeItemQuantityCommand(
        username = username,
        offerId = offerId,
        orderType = Order.OrderType.valueOf(from.orderType),
        newQuantity = from.newQuantity
    )

    override fun fromDomain(from: Basket): BasketDto = BasketMapper.fromDomain(from)
}

class AddExchangeBookMapper(
    val sellerId: UserId,
    val username: String
) : TwoWayMapper<AddExchangeBookToBasketRequest, AddExchangeBookToBasketCommand, Basket, BasketDto> {
    override fun toDomain(from: AddExchangeBookToBasketRequest): AddExchangeBookToBasketCommand = AddExchangeBookToBasketCommand(
        username = username,
        sellerId = sellerId,
        book = AddExchangeBookToBasketCommand.ExchangeBook(
            author = from.author,
            title = from.title,
            isbn = from.isbn,
            condition = Offer.Condition.valueOf(from.condition),
            quantity = from.quantity
        )
    )

    override fun fromDomain(from: Basket): BasketDto = BasketMapper.fromDomain(from)
}
