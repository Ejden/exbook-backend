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
interface ShippingRepository: MongoRepository<ShippingDbModel, String>

@Document("shipping-methods")
data class ShippingDbModel(
    @Id
    val id: String? = null,
    val methodName: String,
    val defaultCost: ShippingCostDbModel
)

data class ShippingCostDbModel(
    val value: Int,
    val currency: String
)

fun ShippingDbModel.toDomain() = ShippingMethod(
    id = ShippingMethodId.of(this.id!!),
    methodName = this.methodName,
    defaultCost = this.defaultCost.toDomain()
)

fun ShippingCostDbModel.toDomain() = Cost(
    value = this.value,
    currency = Currency.valueOf(this.currency)
)
