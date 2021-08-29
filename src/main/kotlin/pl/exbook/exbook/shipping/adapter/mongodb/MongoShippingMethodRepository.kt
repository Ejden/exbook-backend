package pl.exbook.exbook.shipping.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoShippingMethodRepository: MongoRepository<ShippingMethodDocument, String>

@Document("shipping-methods")
data class ShippingMethodDocument(
    @Id
    val id: String? = null,
    val methodName: String,
    val defaultCost: ShippingMethodCostDocument
)

data class ShippingMethodCostDocument(
    val amount: String,
    val currency: String,
    val canBeOverridden: Boolean
)
