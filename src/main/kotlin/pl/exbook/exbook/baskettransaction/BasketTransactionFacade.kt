package pl.exbook.exbook.baskettransaction

import java.time.Duration
import java.time.Instant
import org.springframework.stereotype.Service
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.baskettransaction.domain.DetailedDraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseCreator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseDecorator
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseOrdersRepository
import pl.exbook.exbook.baskettransaction.domain.PreviewBasketTransactionCommand
import pl.exbook.exbook.baskettransaction.domain.PreviewPurchaseCommand
import pl.exbook.exbook.baskettransaction.domain.PurchaseCreationResult
import pl.exbook.exbook.baskettransaction.domain.PurchaseNotCreatedReason
import pl.exbook.exbook.baskettransaction.domain.SuccessfulPurchaseCreationResult
import pl.exbook.exbook.baskettransaction.domain.UnsuccessfulPurchaseCreationResult
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.order.OrderFacade
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.order.domain.PlaceOrdersCommand
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodNotFoundException
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

@Service
class BasketTransactionFacade(
    private val basketFacade: BasketFacade,
    private val draftPurchaseCreator: DraftPurchaseCreator,
    private val userFacade: UserFacade,
    private val offerFacade: OfferFacade,
    private val draftPurchaseDecorator: DraftPurchaseDecorator,
    private val shippingMethodFacade: ShippingMethodFacade,
    private val draftPurchaseRepository: DraftPurchaseOrdersRepository,
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

    fun realisePurchase(username: String): PurchaseCreationResult {
        val timestamp = Instant.now()
        val buyer = userFacade.getUserByUsername(username)
        val draftPurchase = draftPurchaseRepository.getDraftPurchaseForUser(buyer.id)
        val validationErrors = draftPurchase.validatePurchase(timestamp)
        if (validationErrors != null) {
            return validationErrors
        }

        val orders = orderFacade.placeOrders(createPlaceOrdersCommand(draftPurchase!!, buyer, timestamp))
        removeRealisedOrdersFromBasket(buyer, orders, draftPurchase)

        return SuccessfulPurchaseCreationResult(orders.map { it.id })
    }

    fun removeRealisedOrdersFromBasket(
        buyer: User,
        realisedOrders: List<Order>,
        draftPurchase: DraftPurchase
    ) {
        val ordersKeysToRemove = realisedOrders.map { Basket.ItemsGroupKey(it.seller.id, it.orderType) }
        basketFacade.removeGroupsFromBasket(buyer.id, ordersKeysToRemove)
        val updatedPurchase = draftPurchase.removeOrders(realisedOrders.map { it.id })
        draftPurchaseRepository.saveDraftPurchase(updatedPurchase)
    }

    private fun DraftPurchase?.validatePurchase(timestamp: Instant): UnsuccessfulPurchaseCreationResult? {
        if (this == null) {
            return UnsuccessfulPurchaseCreationResult(PurchaseNotCreatedReason.EMPTY_DRAFT)
        }

        if (Duration.between(timestamp, this.lastUpdated).abs().toMinutes() > 30) {
            return UnsuccessfulPurchaseCreationResult(PurchaseNotCreatedReason.DRAFT_TOO_OLD)
        }

        if (!this.orders.isPurchasable()) {
            return UnsuccessfulPurchaseCreationResult(PurchaseNotCreatedReason.DELIVERY_INFO_NOT_COMPLETE)
        }

        return null
    }

    private fun List<DraftPurchase.DraftOrder>.isPurchasable() = this.all {
        it.shipping != null &&
                it.shipping.shippingMethodId in it.availableShippingMethods.map { id -> id.shippingMethodId } &&
                it.shipping.isComplete()
    }

    private fun DraftPurchase.Shipping.isComplete(): Boolean {
        return when (this.shippingMethodType) {
            ShippingMethodType.PERSONAL_DELIVERY -> this.pickupPoint == null && this.shippingAddress == null
            ShippingMethodType.ADDRESS_DELIVERY -> this.pickupPoint == null && this.shippingAddress != null
            ShippingMethodType.PICKUP_DELIVERY -> this.pickupPoint != null && this.shippingAddress == null
        }
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

    private fun createPlaceOrdersCommand(
        draftPurchase: DraftPurchase,
        buyer: User,
        timestamp: Instant
    ) = PlaceOrdersCommand(
        purchaseId = draftPurchase.purchaseId,
        buyer = buyer,
        orders = draftPurchase.orders.map { order ->
          PlaceOrdersCommand.Order(
              orderId = order.orderId,
              items = order.items.map { PlaceOrdersCommand.Item(it.offer.id, it.quantity) },
              seller = PlaceOrdersCommand.Seller(order.seller.id),
              shipping = PlaceOrdersCommand.Shipping(
                  shippingMethodId = order.shipping!!.shippingMethodId,
                  shippingMethodName = order.shipping.shippingMethodName,
                  shippingMethodType = order.shipping.shippingMethodType,
                  shippingAddress = order.shipping.shippingAddress?.let {
                      PlaceOrdersCommand.ShippingAddress(
                          firstAndLastName = it.firstAndLastName,
                          phoneNumber = it.phoneNumber,
                          email = it.email,
                          address = it.address,
                          postalCode = it.postalCode,
                          city = it.city,
                          country = it.country
                      )
                  },
                  pickupPoint = order.shipping.pickupPoint?.let {
                      PlaceOrdersCommand.PickupPoint(
                          firstAndLastName = it.firstAndLastName,
                          phoneNumber = it.phoneNumber,
                          email = it.email,
                          pickupPointId = it.pickupPointId
                      )
                  },
                  cost = PlaceOrdersCommand.ShippingCost(order.shipping.cost.finalCost)
              ),
              exchangeBooks = order.exchangeBooks.map { book ->
                  PlaceOrdersCommand.Book(
                      id = book.id,
                      author = book.author,
                      title = book.title,
                      isbn = book.isbn,
                      condition = book.condition,
                      quantity = book.quantity
                  )
              },
              orderType = order.orderType,
              note = ""
          )
        },
        timestamp = timestamp
    )
}
