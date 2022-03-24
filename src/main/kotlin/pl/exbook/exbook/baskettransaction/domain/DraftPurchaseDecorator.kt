package pl.exbook.exbook.baskettransaction.domain

import org.springframework.stereotype.Component
import pl.exbook.exbook.offer.domain.Offer

@Component
class DraftPurchaseDecorator {
    fun decorateWithDetails(draftPurchase: DraftPurchase, offers: List<Offer>): DetailedDraftPurchase {
        return with(draftPurchase) {
            DetailedDraftPurchase(
                purchaseId = purchaseId,
                buyer = DetailedDraftPurchase.Buyer(
                    buyer.id
                ),
                orders = orders.map { order ->
                    DetailedDraftPurchase.DraftOrder(
                        orderId = order.orderId,
                        orderType = order.orderType,
                        seller = DetailedDraftPurchase.Seller(

                        ),
                        items = order.items.map { item ->
                            DetailedDraftPurchase.Item(

                            )
                        },
                        exchangeBooks = order.exchangeBooks.map { book ->
                            DetailedDraftPurchase.ExchangeBook(

                            )
                        },
                        shipping = DetailedDraftPurchase.Shipping(
                            shippingMethod = DetailedDraftPurchase.ShippingMethod(

                            ),
                            pickupPoint = DetailedDraftPurchase.PickupPoint(

                            ),
                            shippingAddress = DetailedDraftPurchase.ShippingAddress(

                            )
                        )
                    )
                }
            )
        }
    }
}
