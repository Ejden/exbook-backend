package pl.exbook.exbook.baskettransaction

import java.time.Instant
import org.springframework.stereotype.Service
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.baskettransaction.domain.DetailedDraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseCreator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseDecorator
import pl.exbook.exbook.baskettransaction.domain.PreviewBasketTransactionCommand
import pl.exbook.exbook.baskettransaction.domain.PreviewPurchaseCommand
import pl.exbook.exbook.baskettransaction.domain.RealisePurchaseCommand
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.order.OrderFacade
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodNotFoundException
import pl.exbook.exbook.user.UserFacade

@Service
class BasketTransactionFacade(
    private val basketFacade: BasketFacade,
    private val draftPurchaseCreator: DraftPurchaseCreator,
    private val userFacade: UserFacade,
    private val offerFacade: OfferFacade,
    private val draftPurchaseDecorator: DraftPurchaseDecorator,
    private val shippingMethodFacade: ShippingMethodFacade,
    private val orderFacade: OrderFacade
) {
    fun previewPurchase(
        username: String,
        command: PreviewPurchaseCommand
    ): DetailedDraftPurchase {
        val timestamp = Instant.now()
        val buyer = userFacade.getUserByUsername(username)
        val basket = basketFacade.getUserBasket(username)
        val offers = basket.itemsGroups.values.getAllDistinctOffers(timestamp)

        val draftPurchase = draftPurchaseCreator.createDraftPurchase(
            PreviewBasketTransactionCommand(buyer, basket, offers, command.getShipping(), timestamp)
        )

        val sellers = draftPurchase.getAllDistinctSellers()
        val shippingMethods = draftPurchase.getAllDistinctShippingMethods()

        return draftPurchaseDecorator.decorateWithDetails(draftPurchase, offers, sellers, shippingMethods)
    }

    fun realisePurchase(
        username: String,
        command: RealisePurchaseCommand
    ) {

    }

    private fun DraftPurchase.getAllDistinctSellers() = this.orders
        .asSequence()
        .map { it.seller.id }
        .distinct()
        .map { userFacade.getUserById(it) }
        .toList()

    private fun MutableCollection<Basket.ItemsGroup>.getAllDistinctOffers(timestamp: Instant) = this
        .asSequence()
        .flatMap { it.items }
        .distinctBy { it.offer.id }
        .map { offerFacade.getOfferVersion(it.offer.id, timestamp) }
        .toList()

    private fun DraftPurchase.getAllDistinctShippingMethods() = this.orders
        .asSequence()
        .mapNotNull { it.shipping }
        .map {
            shippingMethodFacade
                .getShippingMethod(it.shippingMethodId)
                .orThrowNotFoundException(it.shippingMethodId)
        }
        .toList()

    private fun ShippingMethod?.orThrowNotFoundException(id: ShippingMethodId) = this
        ?: throw ShippingMethodNotFoundException(id)

    private fun PreviewPurchaseCommand.getShipping() = this.orders
        .map {
            if (it.shipping == null) {
                return@map null
            }

            PreviewBasketTransactionCommand.Shipping(
                sellerId = it.sellerId,
                orderType = it.orderType,
                shippingMethodId = it.shipping.shippingMethodId,
                shippingAddress = it.shipping.shippingAddress?.let { address ->
                    PreviewBasketTransactionCommand.ShippingAddress(
                        firstAndLastName = address.firstAndLastName,
                        phoneNumber = address.phoneNumber,
                        email = address.email,
                        address = address.address,
                        postalCode = address.postalCode,
                        city = address.city,
                        country = address.country
                    )
                },
                pickupPoint = it.shipping.pickupPoint?.let { point ->
                    PreviewBasketTransactionCommand.PickupPoint(
                        firstAndLastName = point.firstAndLastName,
                        phoneNumber = point.phoneNumber,
                        email = point.email,
                        pickupPointId = point.pickupPointId
                    )
                }
            )
        }
        .filterNotNull()
}
