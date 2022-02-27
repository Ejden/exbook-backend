package pl.exbook.exbook.shippingmethod.adapter.mongodb

import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shippingmethod.domain.Cost
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodRepository
import org.springframework.stereotype.Component
import pl.exbook.exbook.shared.dto.toDocument

@Component
class DatabaseShippingMethodRepository(
    private val mongoShippingMethodRepository: MongoShippingMethodRepository
) : ShippingMethodRepository {

    override fun findById(shippingMethodId: ShippingMethodId): ShippingMethod? {
        val shippingMethod = mongoShippingMethodRepository.findById(shippingMethodId.raw)
        return if (shippingMethod.isPresent) shippingMethod.get().toDomain() else null
    }

    override fun findByName(name: String): ShippingMethod? {
        return mongoShippingMethodRepository.findByMethodName(name)?.toDomain()
    }

    override fun findAll(): List<ShippingMethod> = mongoShippingMethodRepository.findAll().map { it.toDomain() }

    override fun save(shippingMethod: ShippingMethod): ShippingMethod = mongoShippingMethodRepository
        .save(shippingMethod.toDocument()).toDomain()
}

private fun ShippingMethod.toDocument() = ShippingMethodDocument(
    id = this.id.raw,
    methodName = this.methodName,
    pickupPointMethod = this.pickupPointMethod,
    defaultCost = ShippingMethodCostDocument(
        cost = this.defaultCost.cost.toDocument(),
        canBeOverridden = this.defaultCost.canBeOverridden
    )
)

private fun ShippingMethodDocument.toDomain() = ShippingMethod(
    id = ShippingMethodId(this.id),
    methodName = this.methodName,
    pickupPointMethod = this.pickupPointMethod,
    defaultCost = this.defaultCost.toDomain(),
)

private fun ShippingMethodCostDocument.toDomain() = Cost(
    cost = this.cost.toDomain(),
    canBeOverridden = this.canBeOverridden
)
