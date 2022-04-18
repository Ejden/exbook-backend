package pl.exbook.exbook.baskettransaction.adapter.rest.dto

import pl.exbook.exbook.baskettransaction.domain.DetailedDraftPurchase
import pl.exbook.exbook.baskettransaction.domain.PreviewPurchaseCommand
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDto
import pl.exbook.exbook.util.mapper.TwoWayMapper

object PreviewPurchaseMapper :
    TwoWayMapper<PreviewPurchaseRequest, PreviewPurchaseCommand, DetailedDraftPurchase, DetailedDraftPurchaseDto> {
    override fun toDomain(from: PreviewPurchaseRequest): PreviewPurchaseCommand {
        return PreviewPurchaseCommand(
            orders = from.orders.map {
                PreviewPurchaseCommand.Order(
                    sellerId = UserId(it.sellerId),
                    orderType = Order.OrderType.valueOf(it.orderType),
                    shipping = it.shipping?.toCommand()
                )
            }
        )
    }

    override fun fromDomain(from: DetailedDraftPurchase): DetailedDraftPurchaseDto {
        return DetailedDraftPurchaseDto(
            purchaseId = from.purchaseId.raw,
            buyer = from.buyer.toDto(),
            orders = from.orders.map { it.toDto() },
            totalOffersPrice = from.totalOffersPrice.toDto(),
            totalShippingPrice = from.totalShippingPrice.toDto(),
            totalPrice = from.totalPrice.toDto()
        )
    }

    private fun PreviewPurchaseRequest.Shipping.toCommand() = PreviewPurchaseCommand.Shipping(
        shippingMethodId = ShippingMethodId(this.shippingMethodId),
        shippingAddress = this.shippingAddress?.toCommand(),
        pickupPoint = this.pickupPoint?.toCommand()
    )

    private fun PreviewPurchaseRequest.ShippingAddress.toCommand() = PreviewPurchaseCommand.ShippingAddress(
        firstAndLastName = this.firstAndLastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        address = this.address,
        postalCode = this.postalCode,
        city = this.city,
        country = this.country
    )

    private fun PreviewPurchaseRequest.PickupPoint.toCommand() = PreviewPurchaseCommand.PickupPoint(
        firstAndLastName = this.firstAndLastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        pickupPointId = PickupPointId(this.pickupPointId)
    )

    private fun DetailedDraftPurchase.Buyer.toDto() = DetailedDraftPurchaseDto.Buyer(this.id.raw)

    private fun DetailedDraftPurchase.DraftOrder.toDto() = DetailedDraftPurchaseDto.DraftOrder(
        orderId = this.orderId.raw,
        orderType = this.orderType.name,
        seller = this.seller.toDto(),
        items = this.items.map { it.toDto() },
        exchangeBooks = this.exchangeBooks.map { it.toDto() },
        shipping = this.shipping?.toDto(),
        availableShippingMethods = this.availableShippingMethods.map { it.toDto() },
        totalOffersPrice = this.totalOffersPrice.toDto(),
        totalPrice = this.totalPrice.toDto()
    )

    private fun DetailedDraftPurchase.Seller.toDto() = DetailedDraftPurchaseDto.Seller(
        id = this.id.raw,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username
    )

    private fun DetailedDraftPurchase.Item.toDto() = DetailedDraftPurchaseDto.Item(
        offer = this.offer.toDto(),
        quantity = this.quantity,
        totalPrice = this.totalPrice.toDto()
    )

    private fun DetailedDraftPurchase.ExchangeBook.toDto() = DetailedDraftPurchaseDto.ExchangeBook(
        id = this.id.raw,
        author = this.author,
        title = this.title,
        isbn = this.isbn,
        condition = this.condition.name,
        quantity = this.quantity
    )

    private fun DetailedDraftPurchase.Shipping.toDto() = DetailedDraftPurchaseDto.Shipping(
        shippingMethod = shippingMethod.toDto(),
        pickupPoint = this.pickupPoint?.toDto(),
        shippingAddress = this.shippingAddress?.toDto()
    )

    private fun DetailedDraftPurchase.ShippingOption.toDto() = DetailedDraftPurchaseDto.ShippingOption(
        shippingMethodId = this.shippingMethodId.raw,
        shippingMethodName = this.shippingMethodName,
        pickupPointMethod = this.pickupPointMethod,
        price = this.price.toDto()
    )

    private fun DetailedDraftPurchase.Offer.toDto() = DetailedDraftPurchaseDto.Offer(
        id = this.id.raw,
        price = this.price?.toDto() ?: Money.zeroPln().toDto(),
        book = this.book.toDto(),
        images = this.images.toDto()
    )

    private fun DetailedDraftPurchase.ShippingMethod.toDto() = DetailedDraftPurchaseDto.ShippingMethod(
        id = this.id.raw,
        methodName = this.methodName,
        price = DetailedDraftPurchaseDto.ShippingCost(this.price.finalPrice.toDto())
    )

    private fun DetailedDraftPurchase.PickupPoint.toDto() = DetailedDraftPurchaseDto.PickupPoint(
        firstAndLastName = this.firstAndLastName,
        phoneNumber = phoneNumber,
        email = this.email,
        pickupPointId = this.pickupPointId.raw
    )

    private fun DetailedDraftPurchase.ShippingAddress.toDto() = DetailedDraftPurchaseDto.ShippingAddress(
        firstAndLastName = this.firstAndLastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        address = this.address,
        postalCode = this.postalCode,
        city = this.city,
        country = this.country
    )

    private fun DetailedDraftPurchase.Book.toDto() = DetailedDraftPurchaseDto.Book(
        author = this.author,
        title = this.title,
        condition = this.condition.name,
        isbn = this.isbn
    )

    private fun DetailedDraftPurchase.Images.toDto() = DetailedDraftPurchaseDto.Images(
        thumbnail = this.thumbnail?.toDto(),
        allImages = this.allImages.map { it.toDto() }
    )

    private fun DetailedDraftPurchase.Image.toDto() = DetailedDraftPurchaseDto.Image(this.url)
}
