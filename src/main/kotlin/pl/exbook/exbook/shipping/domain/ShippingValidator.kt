package pl.exbook.exbook.shipping.domain

import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import java.lang.RuntimeException

class ShippingValidator {

    fun validate(shippingMethod: ShippingMethod, selectedShippingRequest: CalculateSelectedShippingCommand) {
        validateAddressAndPickupPoint(shippingMethod, selectedShippingRequest)
    }

    private fun validateAddressAndPickupPoint(shippingMethod: ShippingMethod, selectedShippingRequest: CalculateSelectedShippingCommand) {
        if (shippingMethod.pickupPointMethod) {
            if (selectedShippingRequest.shippingAddress != null) {
                throw ShippingValidationFailedException("Shipping address provided but selected pickup point shipping method")
            }
            if (selectedShippingRequest.pickupPoint == null) {
                throw ShippingValidationFailedException("Selected pickup point shipping method but pickup point is null")
            }
        } else {
            if (selectedShippingRequest.shippingAddress == null) {
                throw ShippingValidationFailedException("Selected shipping method with address but address is null")
            }
            if (selectedShippingRequest.pickupPoint != null) {
                throw ShippingValidationFailedException("Selected shipping method with address but provided pickup point")
            }
        }
    }
}

class ShippingValidationFailedException(errorMsg: String) : RuntimeException(errorMsg)
