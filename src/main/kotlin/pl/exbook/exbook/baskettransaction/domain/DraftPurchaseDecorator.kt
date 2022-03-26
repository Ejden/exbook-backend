package pl.exbook.exbook.baskettransaction.domain

import org.springframework.stereotype.Component
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
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
            DetailedDraftPurchase(
                purchaseId = purchaseId,
                buyer = DetailedDraftPurchase.Buyer(
                    buyer.id
                ),
                orders = orders.map { it.toDetailed(offers, sellers, shippingMethods) },
                totalOffersPrice = totalOffersPrice,
                totalPrice = totalPrice
            )
        }
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
        pickupPointMethod = this.pickupPointMethod,
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
