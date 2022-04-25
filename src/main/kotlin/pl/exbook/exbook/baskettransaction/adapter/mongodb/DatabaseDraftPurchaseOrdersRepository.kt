package pl.exbook.exbook.baskettransaction.adapter.mongodb

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import pl.exbook.exbook.baskettransaction.domain.DraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseOrdersRepository
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDocument
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

@Component
class DatabaseDraftPurchaseOrdersRepository(
    private val repository: MongoDraftPurchaseOrdersRepository
) : DraftPurchaseOrdersRepository {
    override fun getDraftPurchase(
        purchaseId: PurchaseId
    ): DraftPurchase? = repository.findByIdOrNull(purchaseId.raw)?.toDomain()

    override fun getDraftPurchaseForUser(
        userId: UserId
    ): DraftPurchase? = repository.findByBuyer_Id(userId.raw)?.toDomain()

    override fun saveDraftPurchase(
        purchase: DraftPurchase
    ): DraftPurchase = repository.save(purchase.toDocument()).toDomain()
}

private fun DraftPurchaseDocument.toDomain() = DraftPurchase(
    purchaseId = PurchaseId(this.purchaseId),
    buyer = DraftPurchase.Buyer(UserId(this.buyer.id)),
    orders = this.orders.map { it.toDomain() },
    creationDate = this.creationDate,
    lastUpdated = this.lastUpdated,
    totalOffersPrice = this.totalOffersPrice.toDomain(),
    totalShippingPrice = this.totalShippingPrice.toDomain(),
    totalPrice = this.totalPrice.toDomain()
)

private fun DraftPurchaseDocument.DraftOrder.toDomain() = DraftPurchase.DraftOrder(
    orderId = OrderId(this.orderId),
    orderType = OrderType.valueOf(this.orderType),
    seller = DraftPurchase.Seller(UserId(this.seller.id)),
    items = this.items.map { it.toDomain() },
    shipping = this.shipping?.toDomain(),
    availableShippingMethods = this.availableShippingMethods.map { it.toDomain() },
    exchangeBooks = this.exchangeBooks.map { it.toDomain() },
    totalOffersPrice = this.totalOffersPrice.toDomain(),
    totalPrice = this.totalPrice.toDomain()
)

private fun DraftPurchaseDocument.Item.toDomain() = DraftPurchase.Item(
    offer = DraftPurchase.Offer(
        id = OfferId(this.offer.id),
        price = this.offer.price?.toDomain()
    ),
    quantity = this.quantity,
    totalPrice = this.totalPrice.toDomain()
)

private fun DraftPurchaseDocument.Shipping.toDomain() = DraftPurchase.Shipping(
    shippingMethodId = ShippingMethodId(this.shippingMethodId),
    pickupPoint = this.pickupPoint?.let {
        DraftPurchase.PickupPoint(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            pickupPointId = PickupPointId(it.pickupPointId)
        )
    },
    shippingAddress = this.shippingAddress?.let {
        DraftPurchase.ShippingAddress(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            address = it.address,
            postalCode = it.postalCode,
            city = it.city,
            country = it.country
        )
    },
    cost = DraftPurchase.ShippingCost(this.cost.finalCost.toDomain())
)

private fun DraftPurchaseDocument.ShippingOption.toDomain() = DraftPurchase.ShippingOption(
    shippingMethodId = ShippingMethodId(this.shippingMethodId),
    shippingMethodName = this.shippingMethodName,
    shippingMethodType = ShippingMethodType.valueOf(this.shippingMethodType),
    price = this.price.toDomain()
)

private fun DraftPurchaseDocument.ExchangeBook.toDomain() = DraftPurchase.ExchangeBook(
    id = ExchangeBookId(this.id),
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = Offer.Condition.valueOf(this.condition),
    quantity = this.quantity
)

private fun DraftPurchase.toDocument() = DraftPurchaseDocument(
    purchaseId = this.purchaseId.raw,
    buyer = DraftPurchaseDocument.Buyer(this.buyer.id.raw),
    orders = this.orders.map { it.toDocument() },
    creationDate = this.creationDate,
    lastUpdated = this.lastUpdated,
    totalOffersPrice = this.totalOffersPrice.toDocument(),
    totalShippingPrice = this.totalShippingPrice.toDocument(),
    totalPrice = this.totalPrice.toDocument()
)

private fun DraftPurchase.DraftOrder.toDocument() = DraftPurchaseDocument.DraftOrder(
    orderId = this.orderId.raw,
    orderType = this.orderType.name,
    seller = DraftPurchaseDocument.Seller(this.seller.id.raw),
    items = this.items.map { it.toDocument() },
    shipping = this.shipping?.toDocument(),
    availableShippingMethods = this.availableShippingMethods.map { it.toDocument() },
    exchangeBooks = this.exchangeBooks.map { it.toDocument() },
    totalOffersPrice = this.totalOffersPrice.toDocument(),
    totalPrice = this.totalPrice.toDocument()
)

private fun DraftPurchase.Item.toDocument() = DraftPurchaseDocument.Item(
    offer = DraftPurchaseDocument.OfferDocument(
        id = this.offer.id.raw,
        price = this.offer.price?.toDocument()
    ),
    quantity = this.quantity,
    totalPrice = this.totalPrice.toDocument()
)

private fun DraftPurchase.Shipping.toDocument() = DraftPurchaseDocument.Shipping(
    shippingMethodId = this.shippingMethodId.raw,
    pickupPoint = this.pickupPoint?.let {
        DraftPurchaseDocument.PickupPoint(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            pickupPointId = it.pickupPointId.raw
        )
    },
    shippingAddress = this.shippingAddress?.let {
        DraftPurchaseDocument.ShippingAddress(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            address = it.address,
            postalCode = it.postalCode,
            city = it.city,
            country = it.country
        )
    },
    cost = DraftPurchaseDocument.ShippingCostDocument(this.cost.finalCost.toDocument())
)

private fun DraftPurchase.ShippingOption.toDocument() = DraftPurchaseDocument.ShippingOption(
    shippingMethodId = this.shippingMethodId.raw,
    shippingMethodName = this.shippingMethodName,
    shippingMethodType = this.shippingMethodType.name,
    price = this.price.toDocument()
)

private fun DraftPurchase.ExchangeBook.toDocument() = DraftPurchaseDocument.ExchangeBook(
    id = this.id.raw,
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name,
    quantity = this.quantity
)
