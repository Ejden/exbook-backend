package pl.exbook.exbook.offer.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.CategoryNotFoundException
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ValidationException
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade

@Service
class OfferValidator(
    private val shippingMethodFacade: ShippingMethodFacade,
    private val categoryFacade: CategoryFacade
){
    fun validateCreatingOffer(command: CreateOfferCommand) {
        if (command.shippingMethods.any { shippingMethodFacade.getShippingMethod(it.id) == null }) {
            throw ValidationException("Cannot create offer with non existing shipping method")
        }

        try {
            categoryFacade.getCategory(command.category.id)
        } catch (cause: CategoryNotFoundException) {
            throw ValidationException("Cannot create offer with non existing category")
        }
    }

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

            if (!shippingMethod.defaultCost.canBeOverridden && it.price.amount != shippingMethod.defaultCost.cost.amount) {
                throw OfferChangeValidationException(
                    updateCommand.offerId, "Cannot override shipping method cost with id ${shippingMethod.id}"
                )
            }
        }
    }
}

class OfferChangeValidationException(
    offerId: OfferId, message: String
) : ValidationException("Illegal offer change on offer ${offerId.raw} : $message")
