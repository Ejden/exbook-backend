package pl.exbook.exbook.basket

import org.springframework.stereotype.Service
import pl.exbook.exbook.basket.adapter.mongodb.BasketNotFoundException
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.BasketDetailsDecorator
import pl.exbook.exbook.basket.domain.BasketFactory
import pl.exbook.exbook.basket.domain.BasketRepository
import pl.exbook.exbook.basket.domain.BasketValidator
import pl.exbook.exbook.basket.domain.ChangeItemQuantityCommand
import pl.exbook.exbook.basket.domain.DetailedBasket
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade

@Service
class BasketFacade(
    private val basketRepository: BasketRepository,
    private val userFacade: UserFacade,
    private val offerFacade: OfferFacade,
    private val validator: BasketValidator,
    private val basketFactory: BasketFactory,
    private val basketDetailsDecorator: BasketDetailsDecorator
) {
    fun getUserBasket(userId: UserId): Basket {
        return try {
            basketRepository.getUserBasket(userId)
        } catch (cause: BasketNotFoundException) {
            basketRepository.save(basketFactory.createEmptyBasket(userId))
        }
    }

    fun getUserBasket(username: String): Basket {
        val user = userFacade.getUserByUsername(username)

        return try {
            basketRepository.getUserBasket(user.id)
        } catch (cause: BasketNotFoundException) {
            basketRepository.save(basketFactory.createEmptyBasket(user.id))
        }
    }

    fun getDetailedUserBasket(username: String): DetailedBasket {
        val user = userFacade.getUserByUsername(username)
        val basket = getUserBasket(user.id)

        return basketDetailsDecorator.decorateBasketWithDetails(basket)
    }

    fun addItemToBasket(command: AddItemToBasketCommand): Basket {
        val offer = offerFacade.getOffer(command.offerId)
        command.validate(offer)

        val basket = getUserBasket(command.username)

        basket.addToBasket(command.offerId, offer.seller.id, command.orderType, command.quantity)
        return basketRepository.save(basket)
    }

    fun removeItemFromBasket(username: String, offerId: OfferId, orderType: Order.OrderType): Basket {
        val basket = getUserBasket(username)
        basket.removeFromBasket(offerId, orderType)
        return basketRepository.save(basket)
    }

    fun changeItemQuantityInBasket(command: ChangeItemQuantityCommand): Basket {
        val basket = getUserBasket(command.username)
        val offer = offerFacade.getOffer(command.offerId)

        basket.changeItemQuantity(command.offerId, command.newQuantity, offer.seller.id, command.orderType)
        return basketRepository.save(basket)
    }

    private fun AddItemToBasketCommand.validate(offer: Offer): AddItemToBasketCommand {
        validator.validateAddingItem(offer, this)
        return this
    }
}
