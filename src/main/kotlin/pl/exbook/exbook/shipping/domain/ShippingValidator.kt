package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import java.lang.RuntimeException
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

class ShippingValidator {

    fun validate(shippingMethod: ShippingMethod, selectedShippingRequest: CalculateSelectedShippingCommand) {
        validateAddressAndPickupPoint(shippingMethod, selectedShippingRequest)
    }

    private fun validateAddressAndPickupPoint(shippingMethod: ShippingMethod, selectedShippingRequest: CalculateSelectedShippingCommand) {
        if (shippingMethod.type == ShippingMethodType.PICKUP_DELIVERY) {
            if (selectedShippingRequest.shippingAddress != null) {
                throw ShippingValidationFailedException("Shipping address provided but selected pickup point shipping method")
            }
            if (selectedShippingRequest.pickupPoint == null) {
                throw ShippingValidationFailedException("Selected pickup point shipping method but pickup point is null")
            }
        } else if (shippingMethod.type == ShippingMethodType.ADDRESS_DELIVERY) {
            if (selectedShippingRequest.shippingAddress == null) {
                throw ShippingValidationFailedException("Selected shipping method with address but address is null")
            }
            if (selectedShippingRequest.pickupPoint != null) {
                throw ShippingValidationFailedException("Selected shipping method with address but provided pickup point")
            }
        } else {
            if (selectedShippingRequest.pickupPoint != null) {
                throw ShippingValidationFailedException("Selected shipping method with address but provided personal delivery")
            }
            if (selectedShippingRequest.shippingAddress != null) {
                throw ShippingValidationFailedException("Shipping address provided but selected pickup point personal delivery")
            }
        }
    }
}

class ShippingValidationFailedException(errorMsg: String) : RuntimeException(errorMsg)
