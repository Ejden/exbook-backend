package pl.exbook.exbook.shipping.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pl.exbook.exbook.shipping.domain.Cost
import pl.exbook.exbook.shipping.domain.Currency
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.shipping.domain.ShippingMethodId

@Repository
interface ShippingRepository: MongoRepository<ShippingDocument, String>

@Document("shipping-methods")
data class ShippingDocument(
    @Id
    val id: String? = null,
    val methodName: String,
    val defaultCost: ShippingCostDocument
)

data class ShippingCostDocument(
    val value: Int,
    val currency: String,
    val canBeOverridden: Boolean
)

fun ShippingDocument.toDomain() = ShippingMethod(
    id = ShippingMethodId.of(this.id!!),
    methodName = this.methodName,
    defaultCost = this.defaultCost.toDomain()
)

fun ShippingCostDocument.toDomain() = Cost(
    value = this.value,
    currency = Currency.valueOf(this.currency)
)
