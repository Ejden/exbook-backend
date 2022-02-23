package pl.exbook.exbook.baskettransaction.adapter.mongodb

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.exbook.exbook.baskettransaction.domain.DraftPurchase
import pl.exbook.exbook.baskettransaction.domain.DraftPurchaseOrdersRepository
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.PurchaseId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDocument

@Service
class DatabaseDraftPurchaseOrdersRepository(
    private val repository: MongoDraftPurchaseOrdersRepository
) : DraftPurchaseOrdersRepository {
    override fun getDraftPurchase(
        purchaseId: PurchaseId
    ): DraftPurchase? = repository.findByIdOrNull(purchaseId.raw)?.toDomain()

    override fun saveDraftPurchase(
        purchase: DraftPurchase
    ): DraftPurchase = repository.save(purchase.toDocument()).toDomain()
}

private fun DraftPurchaseDocument.toDomain() = DraftPurchase(
    purchaseId = PurchaseId(this.purchaseId),
    buyer = DraftPurchase.Buyer(UserId(this.buyer.id)),
    orders = this.orders.map { it.toDomain() },
    creationDate = this.creationDate,
    lastUpdated = this.lastUpdated
)

private fun DraftPurchaseDocument.DraftOrder.toDomain() = DraftPurchase.DraftOrder(
    orderId = OrderId(this.orderId),
    orderType = OrderType.valueOf(this.orderType),
    seller = DraftPurchase.Seller(UserId(this.seller.id)),
    items = this.items.map { it.toDomain() },
    shipping = this.shipping?.toDomain()
)

private fun DraftPurchaseDocument.Item.toDomain() = DraftPurchase.Item(
    offerId = OfferId(this.offerId),
    quantity = this.quantity,
    price = this.price.toDomain()
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
    }
)

private fun DraftPurchase.toDocument() = DraftPurchaseDocument(
    purchaseId = this.purchaseId.raw,
    buyer = DraftPurchaseDocument.Buyer(this.buyer.id.raw),
    orders = this.orders.map { it.toDocument() },
    creationDate = this.creationDate,
    lastUpdated = this.lastUpdated
)

private fun DraftPurchase.DraftOrder.toDocument() = DraftPurchaseDocument.DraftOrder(
    orderId = this.orderId.raw,
    orderType = this.orderType.name,
    seller = DraftPurchaseDocument.Seller(this.seller.id.raw),
    items = this.items.map { it.toDocument() },
    shipping = this.shipping?.toDocument()
)

private fun DraftPurchase.Item.toDocument() = DraftPurchaseDocument.Item(
    offerId = this.offerId.raw,
    quantity = this.quantity,
    price = this.price.toDocument()
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
    }
)
