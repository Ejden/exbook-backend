package pl.exbook.exbook.baskettransaction.adapter.mongodb

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pl.exbook.exbook.shared.dto.MoneyDocument

@Repository
interface MongoDraftPurchaseOrdersRepository : MongoRepository<DraftPurchaseDocument, String> {
    fun findByBuyer_Id(userId: String): DraftPurchaseDocument?
}

@Document(collection = "draftPurchase")
data class DraftPurchaseDocument(
    @field:Id
    val purchaseId: String,
    val buyer: Buyer,
    val orders: List<DraftOrder>,
    val creationDate: Instant,
    val lastUpdated: Instant,
    val totalOffersPrice: MoneyDocument,
    val totalShippingPrice: MoneyDocument,
    val totalPrice: MoneyDocument
) {
    data class DraftOrder(
        val orderId: String,
        val orderType: String,
        val seller: Seller,
        val items: List<Item>,
        val shipping: Shipping?,
        val availableShippingMethods: List<ShippingOption>,
        val exchangeBooks: List<ExchangeBook>,
        val totalOffersPrice: MoneyDocument,
        val totalPrice: MoneyDocument
    )

    data class Buyer(
        val id: String
    )

    data class Seller(
        val id: String
    )

    data class Item(
        val offer: OfferDocument,
        val quantity: Long,
        val totalPrice: MoneyDocument
    )

    data class OfferDocument(
        val id: String,
        val price: MoneyDocument?
    )

    data class Shipping(
        val shippingMethodId: String,
        val shippingMethodName: String,
        val shippingMethodType: String,
        val pickupPoint: PickupPoint?,
        val shippingAddress: ShippingAddress?,
        val cost: ShippingCostDocument
    )

    data class ShippingOption(
        val shippingMethodId: String,
        val shippingMethodName: String,
        val shippingMethodType: String,
        val price: MoneyDocument
    )

    data class ShippingAddress(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val address: String,
        val postalCode: String,
        val city: String,
        val country: String
    )

    data class PickupPoint(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val pickupPointId: String
    )

    data class ExchangeBook(
        val id: String,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String,
        val quantity: Int
    )

    data class ShippingCostDocument(
        val finalCost: MoneyDocument
    )
}
