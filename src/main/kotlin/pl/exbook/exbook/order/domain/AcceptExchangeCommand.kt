package pl.exbook.exbook.order.domain

import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shipping.domain.Shipping

data class AcceptExchangeCommand(
    val orderId: OrderId,
    val username: String,
    val address: SellerShippingInfoAddress?,
    val pickupPoint: SellerShippingInfoPickupPoint?
) {
    data class SellerShippingInfoAddress(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val address: String,
        val postalCode: String,
        val city: String,
        val country: String
    )

    data class SellerShippingInfoPickupPoint(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val pickupPointId: PickupPointId
    )

    fun toSellerShippingInfo() = Shipping.SellerShippingInfo(
        address = this.address?.let {
            Shipping.ShippingAddress(
                firstAndLastName = it.firstAndLastName,
                phoneNumber = it.phoneNumber,
                email = it.email,
                address = it.address,
                postalCode = it.postalCode,
                city = it.city,
                country = it.country
            )
        },
        pickupPoint = this.pickupPoint?.let {
            Shipping.PickupPoint(
                firstAndLastName = it.firstAndLastName,
                phoneNumber = it.phoneNumber,
                email = it.email,
                pickupPointId = it.pickupPointId
            )
        }
    )
}
