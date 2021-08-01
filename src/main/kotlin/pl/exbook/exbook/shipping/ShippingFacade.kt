package pl.exbook.exbook.shipping

import pl.exbook.exbook.shipping.adapter.mongodb.ShippingCostDocument
import pl.exbook.exbook.shipping.adapter.mongodb.ShippingDocument
import pl.exbook.exbook.shipping.adapter.mongodb.ShippingRepository
import pl.exbook.exbook.shipping.adapter.mongodb.toDomain
import pl.exbook.exbook.shipping.adapter.rest.NewShippingMethod
import pl.exbook.exbook.shipping.domain.Currency
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.util.parseMoneyToInt

class ShippingFacade(
    private val shippingRepository: ShippingRepository
) {

    fun getShippingMethods(): List<ShippingMethod> = shippingRepository.findAll().map { it.toDomain() }

    fun getShippingMethodById(id: String): ShippingMethod = shippingRepository.findById(id)
        .orElseThrow { ShippingMethodNotFoundException(id) }.toDomain()

    fun addShippingMethod(newShippingMethod: NewShippingMethod): ShippingMethod {
        return shippingRepository.save(
            ShippingDocument(
                methodName = newShippingMethod.name,
                defaultCost = ShippingCostDocument(
                    value = parseMoneyToInt(newShippingMethod.cost.defaultCost),
                    currency = Currency.PLN.name,
                    canBeOverridden = newShippingMethod.cost.canBeOverridden
                )
            )
        ).toDomain()
    }
}

class ShippingMethodNotFoundException(id: String): RuntimeException("Shipping method with id $id not found")
