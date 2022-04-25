package pl.exbook.exbook.order.domain

import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.adapter.rest.PlaceOrdersRequest
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId

data class PlaceOrdersCommand(
    val orders: List<Order>
) {
    data class Order(
        val items: List<Item>,
        val seller: Seller,
        val shipping: Shipping,
        val exchangeBooks: List<Book>,
        val orderType: pl.exbook.exbook.order.domain.Order.OrderType,
        val note: String
    )

    data class Item(
        val offerId: OfferId,
        val quantity: Long
    )

    data class Seller(
        val id: UserId
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
        val pickupPointId: PickupPointId
    )

    data class Book(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: Offer.Condition
    )

    companion object {
        fun fromRequest(request: PlaceOrdersRequest) = PlaceOrdersCommand(
            orders = request.orders.map {
                Order(
                    items = it.items.map { item -> Item(OfferId(item.offerId), item.quantity) },
                    seller = Seller(UserId(it.seller.id)),
                    shipping = Shipping(
                        shippingMethodId = ShippingMethodId(it.shipping.shippingMethodId),
                        shippingAddress = it.shipping.shippingAddress?.let { address ->
                            ShippingAddress(
                                firstAndLastName = address.firstAndLastName,
                                phoneNumber = address.phoneNumber,
                                email = address.email,
                                address = address.address,
                                postalCode = address.postalCode,
                                city = address.city,
                                country = address.country
                            )
                        },
                        pickupPoint = it.shipping.pickupPoint?.let { point ->
                            PickupPoint(
                                firstAndLastName = point.firstAndLastName,
                                phoneNumber = point.phoneNumber,
                                email = point.email,
                                pickupPointId = PickupPointId(point.pickupPointId)
                            )
                        }
                    ),
                    exchangeBooks = it.exchangeBooks.map { book ->
                        Book(
                            author = book.author,
                            title = book.title,
                            isbn = book.isbn,
                            condition = Offer.Condition.valueOf(book.condition)
                        )
                    },
                    orderType = pl.exbook.exbook.order.domain.Order.OrderType.valueOf(it.orderType),
                    note = it.note
                )
            }
        )
    }
}
