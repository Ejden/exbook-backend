package pl.exbook.exbook.shippingmethod.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pl.exbook.exbook.shared.dto.MoneyDocument

@Repository
interface MongoShippingMethodRepository: MongoRepository<ShippingMethodDocument, String> {
    fun findByMethodName(methodName: String): ShippingMethodDocument?
}

@Document("shipping-methods")
data class ShippingMethodDocument(
    @Id
    val id: String,
    val methodName: String,
    val pickupPointMethod: Boolean,
    val defaultCost: ShippingMethodCostDocument
)

data class ShippingMethodCostDocument(
    val cost: MoneyDocument,
    val canBeOverridden: Boolean
)
