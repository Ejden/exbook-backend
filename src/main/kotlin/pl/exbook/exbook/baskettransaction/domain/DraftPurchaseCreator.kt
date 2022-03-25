package pl.exbook.exbook.baskettransaction.domain

import java.util.UUID
import org.springframework.stereotype.Service
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shipping.ShippingFacade

@Service
class DraftPurchaseCreator(
    private val draftPurchaseOrdersRepository: DraftPurchaseOrdersRepository,
    private val shippingFacade: ShippingFacade,
    private val draftPurchaseValidator: DraftPurchaseValidator
) {
    fun createDraftPurchase(command: PreviewBasketTransactionCommand): DraftPurchase {
        draftPurchaseValidator.validatePreview(command)
        val oldDraftPurchase = draftPurchaseOrdersRepository.getDraftPurchaseForUser(command.buyer.id)

        if (oldDraftPurchase == null) {
            val newDraft = createNewDraftPurchase(command)
            return draftPurchaseOrdersRepository.saveDraftPurchase(newDraft)
        }

        val updatedDraft = updateDraftPurchase(command, oldDraftPurchase)
        return draftPurchaseOrdersRepository.saveDraftPurchase(updatedDraft)
    }

    private fun createNewDraftPurchase(
        command: PreviewBasketTransactionCommand
    ): DraftPurchase {
        val orders = groupOrders(command)

        return DraftPurchase(
            purchaseId = PurchaseId(UUID.randomUUID().toString()),
            buyer = DraftPurchase.Buyer(command.buyer.id),
            orders = orders,
            creationDate = command.timestamp,
            lastUpdated = command.timestamp,
            totalOffersPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalOffersPrice },
            totalPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalPrice }
        )
    }

    private fun updateDraftPurchase(
        command: PreviewBasketTransactionCommand,
        oldDraftPurchase: DraftPurchase
    ): DraftPurchase {
        val orders = groupOrders(command)

        return DraftPurchase(
            purchaseId = oldDraftPurchase.purchaseId,
            buyer = DraftPurchase.Buyer(command.buyer.id),
            orders = orders,
            creationDate = oldDraftPurchase.creationDate,
            lastUpdated = command.timestamp,
            totalOffersPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalOffersPrice },
            totalPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalPrice }
        )
    }

    private fun groupOrders(command: PreviewBasketTransactionCommand): List<DraftPurchase.DraftOrder> {
        return groupItems(command).entries.map {
            val items = it.key.items.map { item ->
                val offer = command.offers.first { offer -> offer.id == item.offer.id }
                val offerPrice = if (it.key.orderType == OrderType.BUY) offer.price!! else Money.zeroPln()

                DraftPurchase.Item(
                    offer = DraftPurchase.Offer(
                        id = item.offer.id,
                        price = offerPrice
                    ),
                    quantity = item.quantity,
                    totalPrice = offerPrice * item.quantity
                )
            }
            val totalOffersPrice = items.fold(Money.zeroPln()) { acc, x -> acc + x.totalPrice }
            val totalPrice = totalOffersPrice + (it.value?.first?.cost?.finalCost ?: Money.zeroPln())

            DraftPurchase.DraftOrder(
                orderId = OrderId(UUID.randomUUID().toString()),
                orderType = it.key.orderType,
                seller = DraftPurchase.Seller(it.key.sellerId),
                items = items,
                exchangeBooks = it.key.exchangeBooks.map { book ->
                    DraftPurchase.ExchangeBook(
                        id = book.id,
                        author = book.author,
                        title = book.title,
                        isbn = book.isbn,
                        condition = book.condition,
                        quantity = book.quantity
                    )
                },
                shipping = it.value?.let { shipping ->
                    DraftPurchase.Shipping(
                        shippingMethodId = shipping.first.shippingMethodId,
                        pickupPoint = shipping.second.pickupPoint?.let { point ->
                            DraftPurchase.PickupPoint(
                                firstAndLastName = point.firstAndLastName,
                                phoneNumber = point.phoneNumber,
                                email = point.email,
                                pickupPointId = point.pickupPointId
                            )
                        },
                        shippingAddress = shipping.second.shippingAddress?.let { address ->
                            DraftPurchase.ShippingAddress(
                                firstAndLastName = address.firstAndLastName,
                                phoneNumber = address.phoneNumber,
                                email = address.email,
                                address = address.address,
                                postalCode = address.postalCode,
                                city = address.city,
                                country = address.country
                            )
                        },
                        cost = DraftPurchase.ShippingCost(
                            finalCost = shipping.first.cost.finalCost
                        )
                    )
                },
                totalOffersPrice = totalOffersPrice,
                totalPrice = totalPrice
            )
        }
    }

    private fun groupItems(
        command: PreviewBasketTransactionCommand
    ) = command.basket.itemsGroups.values.associateWith { itemsGroup ->
        val shippingCommand = command.shipping.firstOrNull { shipping ->
            shipping.sellerId == itemsGroup.sellerId && shipping.orderType == itemsGroup.orderType
        } ?: return@associateWith null

        val offersToShipping = itemsGroup.items.associateWith { item ->
            command.offers.first { offer -> offer.id == item.offer.id }.shippingMethods
        }.mapKeys { it.key.offer.id }

        Pair(
            shippingFacade.calculateSelectedShipping(
                CalculateSelectedShippingCommand(
                    shippingMethodId = shippingCommand.shippingMethodId,
                    orderItems = itemsGroup.items.map { item ->
                        CalculateSelectedShippingCommand.OrderItem(item.offer.id, item.quantity)
                    },
                    shippingAddress = shippingCommand.shippingAddress?.let {
                        CalculateSelectedShippingCommand.ShippingAddress(
                            firstAndLastName = it.firstAndLastName,
                            phoneNumber = it.phoneNumber,
                            email = it.email,
                            address = it.address,
                            postalCode = it.postalCode,
                            city = it.city,
                            country = it.country
                        )
                    },
                    pickupPoint = shippingCommand.pickupPoint?.let {
                        CalculateSelectedShippingCommand.PickupPoint(
                            firstAndLastName = it.firstAndLastName,
                            phoneNumber = it.phoneNumber,
                            email = it.email,
                            pickupPointId = it.pickupPointId
                        )
                    },
                    offersShippingMethods = offersToShipping
                )
            ), shippingCommand
        )
    }
}
