package pl.exbook.exbook.ability

import io.mockk.mockk
import pl.exbook.exbook.adapters.InMemoryBasketTransactionRepository
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.baskettransaction.BasketTransactionFacade
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseCreator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseDecorator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseValidator
import pl.exbook.exbook.mock.AvailableShippingBuilder
import pl.exbook.exbook.mock.BasketBuilder
import pl.exbook.exbook.mock.BasketFacadeMocks
import pl.exbook.exbook.mock.IdGenerationStrategy
import pl.exbook.exbook.mock.OfferBuilder
import pl.exbook.exbook.mock.OfferFacadeMocks
import pl.exbook.exbook.mock.ShippingBuilder
import pl.exbook.exbook.mock.ShippingFacadeMocks
import pl.exbook.exbook.mock.ShippingMethodBuilder
import pl.exbook.exbook.mock.ShippingMethodFacadeMocks
import pl.exbook.exbook.mock.UserBuilder
import pl.exbook.exbook.mock.UserFacadeMocks
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.order.OrderFacade
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.user.UserFacade

class BasketTransactionAbility {
    private val basketFacade = mockk<BasketFacade>()
    private val userFacade = mockk<UserFacade>()
    private val repository = InMemoryBasketTransactionRepository()
    private val shippingFacade = mockk<ShippingFacade>()
    private val draftPurchaseValidator = DraftPurchaseValidator()
    private val draftPurchaseCreator = DraftPurchaseCreator(repository, shippingFacade, draftPurchaseValidator)
    private val offerFacade = mockk<OfferFacade>()
    private val draftPurchaseDecorator = DraftPurchaseDecorator()
    private val shippingMethodFacade = mockk<ShippingMethodFacade>()
    private val orderFacade = mockk<OrderFacade>()

    private val userFacadeMocks = UserFacadeMocks(userFacade)
    private val basketFacadeMocks = BasketFacadeMocks(basketFacade)
    private val shippingFacadeMocks = ShippingFacadeMocks(shippingFacade)
    private val offerFacadeMocks = OfferFacadeMocks(offerFacade)
    private val shippingMethodFacadeMocks = ShippingMethodFacadeMocks(shippingMethodFacade)

    val facade = BasketTransactionFacade(
        basketFacade = basketFacade,
        draftPurchaseCreator = draftPurchaseCreator,
        userFacade = userFacade,
        offerFacade = offerFacade,
        draftPurchaseDecorator = draftPurchaseDecorator,
        shippingMethodFacade = shippingMethodFacade,
        orderFacade = orderFacade,
        draftPurchaseRepository = repository
    )

    fun thereIsUser(init: UserBuilder.() -> Unit) = userFacadeMocks.thereIsUser(init)
    fun thereIsNoUser(init: UserBuilder.() -> Unit) = userFacadeMocks.thereIsNoUserFor(init)
    fun thereIsBasket(init: BasketBuilder.() -> Unit) = basketFacadeMocks.thereIsBasket(init)
    fun thereIsNoBasketFor(userId: UserId, username: String) = basketFacadeMocks.thereIsNoBasket(userId, username)
    fun thereIsOffer(init: OfferBuilder.() -> Unit) = offerFacadeMocks.thereIsOffer(init)
    fun thereIsNoOffer(offerId: OfferId) = offerFacadeMocks.thereIsNoOfferFor(offerId)
    fun willCreateShipping(idCreationStrategy: IdGenerationStrategy, init: ShippingBuilder.() -> Unit) =
        shippingFacadeMocks.willCreateShipping(idCreationStrategy, init)
    fun thereIsShippingMethod(init: ShippingMethodBuilder.() -> Unit) =
        shippingMethodFacadeMocks.thereIsShippingMethod(init)
    fun willPreviewAvailableShipping(init: AvailableShippingBuilder.() -> Unit) =
        shippingFacadeMocks.willPreviewAvailableShipping(init)
}
