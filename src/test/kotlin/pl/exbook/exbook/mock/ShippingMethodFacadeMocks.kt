package pl.exbook.exbook.mock

import io.mockk.every
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleShippingMethodName
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.domain.Cost
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

class ShippingMethodFacadeMocks(private val shippingMethodFacade: ShippingMethodFacade) {
    fun thereIsShippingMethod(init: ShippingMethodBuilder.() -> Unit) {
        val mockShippingMethod = ShippingMethodBuilder().apply(init).build()
        every { shippingMethodFacade.getShippingMethod(mockShippingMethod.id) } returns mockShippingMethod
        every { shippingMethodFacade.getShippingMethodById(mockShippingMethod.id) } returns mockShippingMethod
    }
}

class ShippingMethodBuilder {
    var id: ShippingMethodId = sampleShippingMethodId
    var methodName: String = sampleShippingMethodName
    var type: ShippingMethodType = ShippingMethodType.PICKUP_DELIVERY
    var defaultCost: Money = "11.99".pln()
    var costCanBeOverridden: Boolean = true

    fun build() = ShippingMethod(
        id = id,
        methodName = methodName,
        type = type,
        defaultCost = Cost(defaultCost, costCanBeOverridden)
    )
}
