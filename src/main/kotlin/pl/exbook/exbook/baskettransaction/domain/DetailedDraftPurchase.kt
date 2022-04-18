package pl.exbook.exbook.baskettransaction.domain

import pl.exbook.exbook.offer.domain.Offer.Condition
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId

data class DetailedDraftPurchase(
    val purchaseId: PurchaseId,
    val buyer: Buyer,
    val orders: List<DraftOrder>,
    val totalOffersPrice: Money,
    val totalShippingPrice: Money,
    val totalPrice: Money
) {
    data class Buyer(
        val id: UserId
    )

    data class DraftOrder(
        val orderId: OrderId,
        val orderType: OrderType,
        val seller: Seller,
        val items: List<Item>,
        val exchangeBooks: List<ExchangeBook>,
        val shipping: Shipping?,
        val availableShippingMethods: List<ShippingOption>,
        val totalOffersPrice: Money,
        val totalPrice: Money
    )

    data class Seller(
        val id: UserId,
        val firstName: String,
        val lastName: String,
        val username: String
    )

    data class Item(
        val offer: Offer,
        val quantity: Long,
        val totalPrice: Money
    )

    data class Shipping(
        val shippingMethod: ShippingMethod,
        val pickupPoint: PickupPoint?,
        val shippingAddress: ShippingAddress?,
    )

    data class ShippingOption(
        val shippingMethodId: ShippingMethodId,
        val shippingMethodName: String,
        val pickupPointMethod: Boolean,
        val price: Money
    )

    data class ShippingMethod(
        val id: ShippingMethodId,
        val methodName: String,
        val price: ShippingCost
    )

    data class Offer(
        val id: OfferId,
        val price: Money?,
        val book: Book,
        val images: Images
    )

    data class Book(
        val author: String,
        val title: String,
        val condition: Condition,
        val isbn: String?
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Image(val url: String)

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

    data class ShippingCost(
        val finalPrice: Money
    )

    data class ExchangeBook(
        val id: ExchangeBookId,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Condition,
        val quantity: Int
    )
}
