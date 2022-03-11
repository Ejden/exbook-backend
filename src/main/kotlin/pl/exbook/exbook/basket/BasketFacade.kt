package pl.exbook.exbook.basket

import java.util.UUID
import org.springframework.stereotype.Service
import pl.exbook.exbook.basket.adapter.mongodb.BasketNotFoundException
import pl.exbook.exbook.basket.domain.AddExchangeBookToBasketCommand
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
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

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
        val buyer = userFacade.getUserByUsername(command.username)
        command.validate(offer, buyer)

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
        command.validate(basket.userId)

        basket.changeItemQuantity(command.offerId, command.newQuantity, offer.seller.id, command.orderType)
        return basketRepository.save(basket)
    }

    fun addExchangeBookToBasket(command: AddExchangeBookToBasketCommand): Basket {
        val basket = getUserBasket(command.username)
        command.validate(basket)

        basket.addExchangeBook(
            command.sellerId,
            Basket.ExchangeBook(
                id = ExchangeBookId(UUID.randomUUID().toString()),
                author = command.book.author,
                title = command.book.title,
                isbn = command.book.isbn,
                condition = command.book.condition,
                quantity = command.book.quantity
            )
        )

        return basketRepository.save(basket)
    }

    fun removeExchangeBookFromBasket(username: String, sellerId: UserId, exchangeBookId: ExchangeBookId): Basket {
        val basket = getUserBasket(username)
        basket.removeExchangeBook(sellerId, exchangeBookId)

        return basketRepository.save(basket)
    }

    private fun AddItemToBasketCommand.validate(offer: Offer, buyer: User): AddItemToBasketCommand {
        validator.validateAddingItem(offer, buyer, this)
        return this
    }

    private fun ChangeItemQuantityCommand.validate(buyerId: UserId): ChangeItemQuantityCommand {
        validator.validateItemQuantityChange(buyerId, this)
        return this
    }

    private fun AddExchangeBookToBasketCommand.validate(basket: Basket): AddExchangeBookToBasketCommand {
        validator.validateAddingBook(this, basket)
        return this
    }
}
