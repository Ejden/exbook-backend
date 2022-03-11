package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId

data class PlaceOrderCommand(
    val items: List<OrderItem>,
    val seller: Seller,
    val shipping: Shipping
) {
    data class OrderItem(
        val offerId: OfferId,
        val orderType: Order.OrderType,
        val exchangeBook: ExchangeBook?,
        val quantity: Int
    )

    data class ExchangeBook(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    data class Shipping(
        val shippingMethodId: ShippingMethodId,
        val shippingAddress: ShippingAddress?,
        val pickupPoint: PickupPoint?
    )

    data class ShippingAddress(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val address: String,
        val postalCode: String,
        val city: String,
        val country: String
    )

    data class PickupPoint(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val pickupPointId: String
    )

    data class Seller(val id: UserId)

//    companion object {
//        fun fromRequest(request: CreateOrderRequest) = PlaceOrderCommand(
//            items = request.items.map { OrderItem(
//                offerId = OfferId(it.offerId),
//                orderType = Order.OrderType.valueOf(it.orderType),
//                exchangeBook = it.exchangeBook?.let { book ->
//                    ExchangeBook(
//                        author = book.author,
//                        title = book.title,
//                        isbn = book.isbn,
//                        condition = Offer.Condition.valueOf(book.condition)
//                    )
//                },
//                quantity = it.quantity
//            ) },
//            seller = Seller(UserId(request.seller.id)),
//            shipping = Shipping(
//                shippingMethodId = ShippingMethodId(request.shipping.shippingMethodId),
//                shippingAddress = request.shipping.shippingAddress?.let {
//                    ShippingAddress(
//                        firstAndLastName = it.firstAndLastName,
//                        phoneNumber = it.phoneNumber,
//                        email = it.email,
//                        address = it.address,
//                        postalCode = it.postalCode,
//                        city = it.city,
//                        country = it.country
//                    )
//                },
//                pickupPoint = request.shipping.pickupPoint?.let {
//                    PickupPoint(
//                        firstAndLastName = it.firstAndLastName,
//                        phoneNumber = it.phoneNumber,
//                        email = it.email,
//                        pickupPointId = it.pickupPointId
//                    )
//                }
//            )
//        )
//    }
}
