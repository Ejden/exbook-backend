package pl.exbook.exbook.basket

import pl.exbook.exbook.basket.adapter.mongodb.BasketNotFoundException
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.BasketFactory
import pl.exbook.exbook.basket.domain.BasketRepository
import pl.exbook.exbook.basket.domain.BasketValidator
import pl.exbook.exbook.basket.domain.DetailedBasket
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade

class BasketFacade(
    private val basketRepository: BasketRepository,
    private val userFacade: UserFacade,
    private val offerFacade: OfferFacade,
    private val validator: BasketValidator,
    private val basketFactory: BasketFactory
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
        val detailedItems = basket.items.toDetailed()

        return basket.toDetailed(detailedItems)
    }

    fun addItemToBasket(username: String, command: AddItemToBasketCommand): Basket {
        val offer = offerFacade.getOffer(command.offerId)
        command.validate(offer)

        val basket = getUserBasket(username)

        basket.addToBasket(command.offerId, offer.price!!, command.quantity)
        return basketRepository.save(basket)
    }

    fun removeItemFromBasket(username: String, offerId: OfferId): Basket {
        val basket = getUserBasket(username)
        basket.removeFromBasket(offerId)
        return basketRepository.save(basket)
    }

    fun changeItemQuantityInBasket(username: String, offerId: OfferId, quantity: Long): Basket {
        val basket = getUserBasket(username)
        basket.changeItemQuantity(offerId, quantity)
        return basketRepository.save(basket)
    }

    private fun AddItemToBasketCommand.validate(offer: Offer): AddItemToBasketCommand {
        validator.validate(offer)
        return this
    }

    private fun List<Basket.Item>.toDetailed() = this.map {
        val offer = offerFacade.getOffer(it.offerId)
        val seller = userFacade.getUserById(offer.seller.id)

        DetailedBasket.Item(
            DetailedBasket.Offer(
                id = it.offerId,
                price = it.offerPrice,
                book = DetailedBasket.Book(
                    author = offer.book.author,
                    title = offer.book.title
                ),
                images = DetailedBasket.Images(
                    thumbnail = offer.images.thumbnail?.let { img -> DetailedBasket.Image(img.url) },
                    otherImages = offer.images.allImages.map { img -> DetailedBasket.Image(img.url) }
                ),
                seller = DetailedBasket.Seller(
                    id = offer.seller.id,
                    firstName = seller.firstName,
                    lastName = seller.lastName
                )
            ),
            quantity = it.quantity
        )
    }
}
