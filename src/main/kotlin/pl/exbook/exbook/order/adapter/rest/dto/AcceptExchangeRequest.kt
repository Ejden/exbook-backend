package pl.exbook.exbook.order.adapter.rest.dto

import pl.exbook.exbook.order.domain.AcceptExchangeCommand
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId

data class AcceptExchangeRequest(
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
        val pickupPointId: String
    )

    fun toCommand(orderId: OrderId, username: String) = AcceptExchangeCommand(
        orderId = orderId,
        username = username,
        address = this.address?.let {
            AcceptExchangeCommand.SellerShippingInfoAddress(
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
            AcceptExchangeCommand.SellerShippingInfoPickupPoint(
                firstAndLastName = it.firstAndLastName,
                phoneNumber = it.phoneNumber,
                email = it.email,
                pickupPointId = PickupPointId(it.pickupPointId)
            )
        }
    )
}
