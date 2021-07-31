package pl.exbook.exbook.shipping

import org.springframework.stereotype.Service
import pl.exbook.exbook.shipping.adapter.mongodb.ShippingCostDbModel
import pl.exbook.exbook.shipping.adapter.mongodb.ShippingDbModel
import pl.exbook.exbook.shipping.adapter.mongodb.ShippingRepository
import pl.exbook.exbook.shipping.adapter.mongodb.toDomain
import pl.exbook.exbook.shipping.adapter.rest.NewShippingMethod
import pl.exbook.exbook.shipping.domain.Currency
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.util.parseMoneyToInt

@Service
class ShippingFacade(
    val shippingRepository: ShippingRepository
) {

    fun getShippingMethods(): List<ShippingMethod> = shippingRepository.findAll().map { it.toDomain() }

    fun addShippingMethod(newShippingMethod: NewShippingMethod): ShippingMethod {
        return shippingRepository.save(
            ShippingDbModel(
                methodName = newShippingMethod.name,
                defaultCost = ShippingCostDbModel(
                    value = parseMoneyToInt(newShippingMethod.defaultCost),
                    currency = Currency.PLN.name
                )
            )
        ).toDomain()
    }
}