package pl.exbook.exbook.shipping.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

import pl.exbook.exbook.shared.dto.MoneyDocument

@Repository
interface MongoShippingRepository: MongoRepository<ShippingDocument, String>

@Document("shipping")
abstract class ShippingDocument(
    @Id
    val id: String?,
    val shippingMethodId: String,
    val shippingMethodName: String,
    val sellerInfo: SellerShippingInfoDocument?,
    val cost: DeliveryCostDocument
)

class PickupPointShippingDocument(
    id: String?,
    shippingMethodId: String,
    shippingMethodName: String,
    sellerInfo: SellerShippingInfoDocument?,
    cost: DeliveryCostDocument,
    val pickupPoint: PickupPointDocument
) : ShippingDocument(id, shippingMethodId, shippingMethodName, sellerInfo, cost)

class AddressShippingDocument(
    id: String?,
    shippingMethodId: String,
    shippingMethodName: String,
    sellerInfo: SellerShippingInfoDocument?,
    cost: DeliveryCostDocument,
    val address: AddressDocument
) : ShippingDocument(id, shippingMethodId, shippingMethodName, sellerInfo, cost)

class PersonalShippingDocument(
    id: String?,
    shippingMethodId: String,
    shippingMethodName: String,
    sellerInfo: SellerShippingInfoDocument?,
    cost: DeliveryCostDocument
) : ShippingDocument(id, shippingMethodId, shippingMethodName, sellerInfo, cost)

data class SellerShippingInfoDocument(
    val address: AddressDocument?,
    val pickupPoint: PickupPointDocument?
)

data class AddressDocument(
    val firstAndLastName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val postalCode: String,
    val city: String,
    val country: String
)

data class PickupPointDocument(
    val firstAndLastName: String,
    val phoneNumber: String,
    val email: String,
    val pickupPointId: String
)

data class DeliveryCostDocument(
    val finalCost: MoneyDocument
)
