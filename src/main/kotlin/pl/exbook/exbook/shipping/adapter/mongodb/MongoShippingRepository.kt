package pl.exbook.exbook.shipping.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoShippingRepository: MongoRepository<ShippingDocument, String>

@Document("shipping")
abstract class ShippingDocument(
    @Id
    val id: String?,
    val shippingMethodId: String,
)

class PickupPointShippingDocument(
    id: String?,
    shippingMethodId: String,
    val pickupPoint: PickupPointDocument
) : ShippingDocument(id, shippingMethodId)

class AddressShippingDocument(
    id: String?,
    shippingMethodId: String,
    val address: AddressDocument
) : ShippingDocument(id, shippingMethodId)

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
