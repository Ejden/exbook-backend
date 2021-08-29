package pl.exbook.exbook.shipping.adapter.mongodb

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shipping.adapter.rest.NewShippingMethod
import pl.exbook.exbook.shipping.domain.Cost
import pl.exbook.exbook.shipping.domain.Currency
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.shipping.domain.ShippingMethodRepository
import java.math.BigDecimal

class DatabaseShippingMethodRepository(
    private val mongoShippingMethodRepository: MongoShippingMethodRepository
) : ShippingMethodRepository {

    override fun findById(shippingMethodId: ShippingMethodId): ShippingMethod? {
        val shippingMethod = mongoShippingMethodRepository.findById(shippingMethodId.raw)
        return if (shippingMethod.isPresent) shippingMethod.get().toDomain() else null
    }

    override fun findAll(): List<ShippingMethod> = mongoShippingMethodRepository.findAll().map { it.toDomain() }

    override fun save(newShippingMethod: NewShippingMethod): ShippingMethod = mongoShippingMethodRepository
        .save(newShippingMethod.toDocument()).toDomain()
}

private fun NewShippingMethod.toDocument() = ShippingMethodDocument(
    methodName = this.name,
    defaultCost = ShippingMethodCostDocument(
        amount = this.cost.defaultCost,
        currency = Currency.PLN.name,
        canBeOverridden = this.cost.canBeOverridden
    )
)

fun ShippingMethodDocument.toDomain() = ShippingMethod(
    id = ShippingMethodId(this.id!!),
    methodName = this.methodName,
    defaultCost = this.defaultCost.toDomain()
)

fun ShippingMethodCostDocument.toDomain() = Cost(
    amount = BigDecimal(this.amount),
    currency = Currency.valueOf(this.currency),
    canBeOverridden = this.canBeOverridden
)
