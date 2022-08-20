package pl.exbook.exbook.order.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.shipping.domain.AddressShipping
import pl.exbook.exbook.shipping.domain.PersonalShipping
import pl.exbook.exbook.shipping.domain.PickupPointShipping
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType
import pl.exbook.exbook.user.domain.User

@Service
class OrderDecorator(private val offerFacade: OfferFacade) {
    fun decorate(order: Order, seller: User, buyer: User, shipping: Shipping): OrderSnippet {
        val items = order.items.map { item -> toOrderSnippetItem(item) }
        return order.toOrderSnippet(seller, buyer, shipping, items)
    }

    private fun Order.toOrderSnippet(
        seller: User,
        buyer: User,
        shipping: Shipping,
        orderItems: List<OrderSnippet.OrderItem>
    ) = OrderSnippet(
        id = this.id,
        buyer = OrderSnippet.Buyer(
            id = this.buyer.id,
            name = buyer.username,
            firstName = buyer.firstName,
            lastName = buyer.lastName
        ),
        seller = OrderSnippet.Seller(seller.id, seller.username, seller.firstName, seller.lastName),
        sellerShippingInfo = this.shipping.sellerShippingInfo?.let {
           OrderSnippet.SellerShippingInfo(
               address = it.address?.let { address ->
               OrderSnippet.ShippingAddress(
                   firstAndLastName = address.firstAndLastName,
                   phoneNumber = address.phoneNumber,
                   email = address.email,
                   address = address.address,
                   postalCode = address.postalCode,
                   city = address.city,
                   country = address.country
               )
               },
               pickupPoint = it.pickupPoint?.let { pickupPoint ->
                   OrderSnippet.PickupPoint(
                       firstAndLastName = pickupPoint.firstAndLastName,
                       phoneNumber = pickupPoint.phoneNumber,
                       email = pickupPoint.email,
                       pickupPointId = pickupPoint.pickupPointId
                   )
               }
           )
        },
        shipping = OrderSnippet.Shipping(
            id = this.shipping.id,
            methodName = shipping.shippingMethodName,
            methodType = when (shipping) {
                is PersonalShipping -> ShippingMethodType.PERSONAL_DELIVERY
                is AddressShipping -> ShippingMethodType.ADDRESS_DELIVERY
                is PickupPointShipping -> ShippingMethodType.PICKUP_DELIVERY
                else -> throw IllegalStateException("")
            },
            shippingAddress = shipping.let {
                if (it is AddressShipping) {
                    OrderSnippet.ShippingAddress(
                        firstAndLastName = it.address.firstAndLastName,
                        phoneNumber = it.address.phoneNumber,
                        email = it.address.email,
                        address = it.address.address,
                        postalCode = it.address.postalCode,
                        city = it.address.city,
                        country = it.address.country
                    )
                } else null
            },
            pickupPoint = shipping.let {
                if (it is PickupPointShipping) {
                    OrderSnippet.PickupPoint(
                        firstAndLastName = it.pickupPoint.firstAndLastName,
                        phoneNumber = it.pickupPoint.phoneNumber,
                        email = it.pickupPoint.email,
                        pickupPointId = it.pickupPoint.pickupPointId
                    )
                } else null
            },
            cost = OrderSnippet.Cost(shipping.cost.finalCost)
        ),
        items = orderItems,
        orderType = this.orderType,
        exchangeBooks = this.exchangeBooks,
        orderDate = this.orderDate,
        status = this.status,
        totalCost = this.totalCost,
        note = this.note,
        availableActions = OrderSnippet.Actions(
            buyerActions = OrderSnippet.BuyerActions(
                canBeReturned = this.canBeReturned,
                canBeCancelled = this.canBeCancelled,
                canBeMarkedAsDelivered = this.canBeMarkedAsDelivered
            ),
            sellerActions = OrderSnippet.SellerActions(
                canBeCancelled = this.canBeCancelled,
                canExchangeBeDismissed = this.canExchangeBeDismissed,
                canExchangeBeAccepted = this.canExchangeBeAccepted,
                canBeMarkedAsSent = this.canBeMarkedAsSent,
                canBeMarkedAsReturnDelivered = this.canBeMarkedAsReturnDelivered
            )
        )
    )

    private fun toOrderSnippetItem(item: Order.OrderItem): OrderSnippet.OrderItem {
        val offer = offerFacade.getOffer(item.offerId)
        return OrderSnippet.OrderItem(
            offerId = offer.id,
            book = OrderSnippet.Book(
                author = offer.book.author,
                title = offer.book.title
            ),
            images = OrderSnippet.Images(offer.images.thumbnail?.let { OrderSnippet.Image(it.url) }),
            quantity = item.quantity,
            cost = item.cost
        )
    }
}
