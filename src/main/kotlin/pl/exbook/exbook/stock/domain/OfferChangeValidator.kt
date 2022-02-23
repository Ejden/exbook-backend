package pl.exbook.exbook.stock.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.UpdateOfferCommand
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade

@Service
class OfferChangeValidator(
    private val shippingMethodFacade: ShippingMethodFacade
){
    fun validateOfferChange(currentOffer: Offer, updateCommand: UpdateOfferCommand) {
        if ((updateCommand.type == Offer.Type.BUY_ONLY || updateCommand.type == Offer.Type.EXCHANGE_AND_BUY) && updateCommand.price == null) {
            throw OfferChangeValidationException(updateCommand.offerId, "Missing price for buy offer type")
        }

        if (updateCommand.type == Offer.Type.EXCHANGE_ONLY && updateCommand.price != null) {
            throw OfferChangeValidationException(updateCommand.offerId, "Price should be empty for exchange offer type")
        }

        if (updateCommand.shippingMethods.isEmpty()) {
            throw OfferChangeValidationException(updateCommand.offerId, "Offer should have at least one shipping method")
        }

        updateCommand.shippingMethods.forEach {
            val shippingMethod = shippingMethodFacade.getShippingMethod(it.id)

            if (shippingMethod == null) {
                throw OfferChangeValidationException(
                    updateCommand.offerId, "Shipping method with id ${it.id.raw} doesn't exist"
                )
            }

            if (!shippingMethod.defaultCost.canBeOverridden && it.price.amount != shippingMethod.defaultCost.amount) {
                throw OfferChangeValidationException(
                    updateCommand.offerId, "Cannot override shipping method cost with id ${shippingMethod.id}"
                )
            }
        }
    }
}

class OfferChangeValidationException(
    offerId: OfferId, message: String
) : RuntimeException("Illegal offer change on offer ${offerId.raw} : $message")
