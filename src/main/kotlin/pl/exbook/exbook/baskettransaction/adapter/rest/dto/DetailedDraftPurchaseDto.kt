package pl.exbook.exbook.baskettransaction.adapter.rest.dto

import pl.exbook.exbook.shared.dto.MoneyDto

data class DetailedDraftPurchaseDto(
    val purchaseId: String,
    val buyer: Buyer,
    val orders: List<DraftOrder>,
    val totalOffersPrice: MoneyDto,
    val totalPrice: MoneyDto
) {
    data class Buyer(
        val id: String
    )

    data class DraftOrder(
        val orderId: String,
        val orderType: String,
        val seller: Seller,
        val items: List<Item>,
        val exchangeBooks: List<ExchangeBook>,
        val shipping: Shipping?,
        val totalOffersPrice: MoneyDto,
        val totalPrice: MoneyDto
    )

    data class Seller(
        val id: String,
        val firstName: String,
        val lastName: String,
        val username: String
    )

    data class Item(
        val offer: Offer,
        val quantity: Long,
        val totalPrice: MoneyDto
    )

    data class Shipping(
        val shippingMethod: ShippingMethod,
        val pickupPoint: PickupPoint?,
        val shippingAddress: ShippingAddress?,
    )

    data class ShippingMethod(
        val id: String,
        val methodName: String,
        val price: ShippingCost
    )

    data class Offer(
        val id: String,
        val price: MoneyDto,
        val book: Book,
        val images: Images
    )

    data class Book(
        val author: String,
        val title: String,
        val condition: String,
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
        val pickupPointId: String
    )

    data class ShippingCost(
        val finalPrice: MoneyDto
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
