package pl.exbook.exbook.baskettransaction.adapter.rest.dto

data class PreviewPurchaseRequest(
    val orders: List<Order>
) {
    data class Order(
        val sellerId: String,
        val orderType: String,
        val shipping: Shipping?
    )

    data class Shipping(
        val shippingMethodId: String,
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
}
