package pl.exbook.exbook.shippingmethod.adapter.mongodb

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shippingmethod.adapter.rest.NewShippingMethod
import pl.exbook.exbook.shippingmethod.domain.Cost
import pl.exbook.exbook.shippingmethod.domain.Currency
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodRepository
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
    pickupPointMethod = this.pickupPointMethod,
    defaultCost = ShippingMethodCostDocument(
        amount = this.cost.defaultCost,
        currency = Currency.PLN.name,
        canBeOverridden = this.cost.canBeOverridden
    )
)

fun ShippingMethodDocument.toDomain() = ShippingMethod(
    id = ShippingMethodId(this.id!!),
    methodName = this.methodName,
    pickupPointMethod = this.pickupPointMethod,
    defaultCost = this.defaultCost.toDomain(),
)

fun ShippingMethodCostDocument.toDomain() = Cost(
    amount = BigDecimal(this.amount),
    currency = Currency.valueOf(this.currency),
    canBeOverridden = this.canBeOverridden
)
