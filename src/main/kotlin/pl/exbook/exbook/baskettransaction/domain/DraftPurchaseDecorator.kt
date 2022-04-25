package pl.exbook.exbook.baskettransaction.domain

import org.springframework.stereotype.Component
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType
import pl.exbook.exbook.user.domain.User

@Component
class DraftPurchaseDecorator {
    fun decorateWithDetails(
        draftPurchase: DraftPurchase,
        offers: List<Offer>,
        sellers: List<User>,
        shippingMethods: List<ShippingMethod>
    ): DetailedDraftPurchase {
        return with(draftPurchase) {
            val orders = orders.map { it.toDetailed(offers, sellers, shippingMethods) }
            DetailedDraftPurchase(
                purchaseId = purchaseId,
                buyer = DetailedDraftPurchase.Buyer(
                    buyer.id
                ),
                orders = orders,
                totalOffersPrice = totalOffersPrice,
                totalShippingPrice = totalShippingPrice,
                totalPrice = totalPrice,
                isPurchasable = orders.isPurchasable(shippingMethods),
                isShippingInfoComplete = orders.isShippingInfoComplete(shippingMethods)
            )
        }
    }

    private fun List<DetailedDraftPurchase.DraftOrder>.isPurchasable(shippingMethods: List<ShippingMethod>) = this.all {
        it.shipping != null &&
                it.shipping.shippingMethod.id in it.availableShippingMethods.map { id -> id.shippingMethodId } &&
                it.shipping.isComplete(shippingMethods)
    }

    private fun DetailedDraftPurchase.Shipping.isComplete(shippingMethods: List<ShippingMethod>): Boolean {
        val shippingMethod = shippingMethods.first { it.id == this.shippingMethod.id }
        return when (shippingMethod.type) {
            ShippingMethodType.PERSONAL_DELIVERY -> this.pickupPoint == null && this.shippingAddress == null
            ShippingMethodType.ADDRESS_DELIVERY -> this.pickupPoint == null && this.shippingAddress != null
            ShippingMethodType.PICKUP_DELIVERY -> this.pickupPoint != null && this.shippingAddress == null
        }
    }

    private fun List<DetailedDraftPurchase.DraftOrder>.isShippingInfoComplete(
        shippingMethods: List<ShippingMethod>
    ) = this.all {
        it.shipping != null &&
                it.shipping.shippingMethod.id in it.availableShippingMethods.map { id -> id.shippingMethodId } &&
                it.shipping.isComplete(shippingMethods)
    }

    private fun DraftPurchase.DraftOrder.toDetailed(
        offers: List<Offer>,
        sellers: List<User>,
        shippingMethods: List<ShippingMethod>
    ): DetailedDraftPurchase.DraftOrder {
        val seller = sellers.first { it.id == seller.id }

        return DetailedDraftPurchase.DraftOrder(
            orderId = orderId,
            orderType = orderType,
            seller = seller.toDetailedSeller(),
            items = items.map { it.toDetailed(offers) },
            exchangeBooks = exchangeBooks.map { it.toDetailed() },
            shipping = shipping?.toDetailed(shippingMethods),
            availableShippingMethods = availableShippingMethods.map { it.toDetailed() },
            totalOffersPrice = totalOffersPrice,
            totalPrice = totalPrice
        )
    }

    private fun User.toDetailedSeller() = DetailedDraftPurchase.Seller(
        id = id,
        firstName = firstName,
        lastName = lastName,
        username = username
    )

    private fun DraftPurchase.Item.toDetailed(offers: List<Offer>): DetailedDraftPurchase.Item {
        val offer = offers.first { it.id == offer.id }

        return DetailedDraftPurchase.Item(
            offer = DetailedDraftPurchase.Offer(
                id = offer.id,
                price = this.offer.price,
                book = DetailedDraftPurchase.Book(
                    author = offer.book.author,
                    title = offer.book.title,
                    condition = offer.book.condition,
                    isbn = offer.book.isbn
                ),
                images = DetailedDraftPurchase.Images(
                    thumbnail = offer.images.thumbnail?.let { DetailedDraftPurchase.Image(it.url) },
                    allImages = offer.images.allImages.map { DetailedDraftPurchase.Image(it.url) }
                )
            ),
            quantity = quantity,
            totalPrice = totalPrice
        )
    }

    private fun DraftPurchase.ExchangeBook.toDetailed() = DetailedDraftPurchase.ExchangeBook(
        id = id,
        author = author,
        title = title,
        isbn = isbn,
        condition = condition,
        quantity = quantity
    )

    private fun DraftPurchase.Shipping.toDetailed(
        shippingMethods: List<ShippingMethod>
    ): DetailedDraftPurchase.Shipping {
        val shippingMethod = shippingMethods.first { method -> method.id == shippingMethodId }

        return DetailedDraftPurchase.Shipping(
            shippingMethod = DetailedDraftPurchase.ShippingMethod(
                id = shippingMethodId,
                methodName = shippingMethod.methodName,
                price = DetailedDraftPurchase.ShippingCost(
                    finalPrice = cost.finalCost
                )
            ),
            pickupPoint = pickupPoint?.toDetailed(),
            shippingAddress = shippingAddress?.toDetailed()
        )
    }

    private fun DraftPurchase.ShippingOption.toDetailed() = DetailedDraftPurchase.ShippingOption(
        shippingMethodId = this.shippingMethodId,
        shippingMethodName = this.shippingMethodName,
        shippingMethodType = this.shippingMethodType,
        price = this.price
    )

    private fun DraftPurchase.PickupPoint.toDetailed() = DetailedDraftPurchase.PickupPoint(
        firstAndLastName = firstAndLastName,
        phoneNumber = phoneNumber,
        email = email,
        pickupPointId = pickupPointId
    )

    private fun DraftPurchase.ShippingAddress.toDetailed() = DetailedDraftPurchase.ShippingAddress(
        firstAndLastName = firstAndLastName,
        phoneNumber = phoneNumber,
        email = email,
        address = address,
        postalCode = postalCode,
        city = city,
        country = country
    )
}
