package pl.exbook.exbook.baskettransaction.domain

import java.util.UUID
import org.springframework.stereotype.Service
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shipping.CalculateSelectedShippingCommand
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.AvailableShipping
import pl.exbook.exbook.shipping.domain.PreviewAvailableShippingCommand

@Service
class DraftPurchaseCreator(
    private val draftPurchaseOrdersRepository: DraftPurchaseOrdersRepository,
    private val shippingFacade: ShippingFacade,
    private val draftPurchaseValidator: DraftPurchaseValidator
) {
    fun createDraftPurchase(command: PreviewBasketTransactionCommand): DraftPurchase {
        draftPurchaseValidator.validatePreview(command)
        val oldDraftPurchase = draftPurchaseOrdersRepository.getDraftPurchaseForUser(command.buyer.id)
        val availableShipping = shippingFacade.previewAvailableShipping(
            PreviewAvailableShippingCommand(command.timestamp, command.toPreviewOrdersShippingCommand())
        )

        if (oldDraftPurchase == null) {
            val newDraft = createNewDraftPurchase(command, availableShipping)
            return draftPurchaseOrdersRepository.saveDraftPurchase(newDraft)
        }

        val updatedDraft = updateDraftPurchase(command, oldDraftPurchase, availableShipping)
        return draftPurchaseOrdersRepository.saveDraftPurchase(updatedDraft)
    }

    private fun createNewDraftPurchase(
        command: PreviewBasketTransactionCommand,
        availableShipping: AvailableShipping
    ): DraftPurchase {
        val orders = groupOrders(command, availableShipping)

        return DraftPurchase(
            purchaseId = PurchaseId(UUID.randomUUID().toString()),
            buyer = DraftPurchase.Buyer(command.buyer.id),
            orders = orders,
            creationDate = command.timestamp,
            lastUpdated = command.timestamp,
            totalOffersPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalOffersPrice },
            totalShippingPrice = orders.fold(Money.zeroPln()) { acc, x ->
                acc + (x.shipping?.cost?.finalCost ?: Money.zeroPln())
            },
            totalPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalPrice }
        )
    }

    private fun updateDraftPurchase(
        command: PreviewBasketTransactionCommand,
        oldDraftPurchase: DraftPurchase,
        availableShipping: AvailableShipping
    ): DraftPurchase {
        val orders = groupOrders(command, availableShipping, oldDraftPurchase)

        return DraftPurchase(
            purchaseId = oldDraftPurchase.purchaseId,
            buyer = DraftPurchase.Buyer(command.buyer.id),
            orders = orders,
            creationDate = oldDraftPurchase.creationDate,
            lastUpdated = command.timestamp,
            totalOffersPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalOffersPrice },
            totalShippingPrice = orders.fold(Money.zeroPln()) { acc, x ->
                acc + (x.shipping?.cost?.finalCost ?: Money.zeroPln())
            },
            totalPrice = orders.fold(Money.zeroPln()) { acc, x -> acc + x.totalPrice }
        )
    }

    private fun groupOrders(
        command: PreviewBasketTransactionCommand,
        availableShipping: AvailableShipping,
        oldDraftPurchase: DraftPurchase? = null
    ): List<DraftPurchase.DraftOrder> {
        return groupItems(command).entries.map {
            val oldOrder = oldDraftPurchase?.orders?.firstOrNull { oldOrder ->
                oldOrder.seller.id == it.key.sellerId && oldOrder.orderType == it.key.orderType
            }
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
            val availableShippingOptions = availableShipping.getOptionsFor(it.key.sellerId, it.key.orderType)
            val oldShippingIsStillValid = availableShippingOptions.any { option ->
                option.shippingMethodId == oldOrder?.shipping?.shippingMethodId &&
                        option.price == oldOrder.shipping.cost.finalCost
            }
            val shipping = it.value?.let { shipping ->
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
            } ?: (if (oldShippingIsStillValid) oldOrder?.shipping else null)
            val totalPrice = totalOffersPrice + (shipping?.cost?.finalCost ?: Money.zeroPln())

            DraftPurchase.DraftOrder(
                orderId = oldOrder?.orderId ?: OrderId(UUID.randomUUID().toString()),
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
                shipping = shipping,
                availableShippingMethods = availableShippingOptions,
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

    private fun PreviewBasketTransactionCommand.toPreviewOrdersShippingCommand() = this.basket.itemsGroups
        .mapKeys { PreviewAvailableShippingCommand.OrderKey(it.key.sellerId, it.key.orderType) }
        .mapValues {
            PreviewAvailableShippingCommand.Order(
                it.value.items.map { item -> this.offers.first { offer -> offer.id == item.offer.id } })
        }

    private fun AvailableShipping.getOptionsFor(sellerId: UserId, orderType: OrderType) = this.shippingByOrders
        .filter { it.key.sellerId == sellerId && it.key.orderType == orderType }
        .values
        .firstOrNull()
        .orEmpty()
        .map { DraftPurchase.ShippingOption(it.methodId, it.methodName, it.type, it.price) }
}
